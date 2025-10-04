package me.doruk.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.model.CartItem;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.entity.OrderItem;
import me.doruk.orderservice.entity.ProcessedCartId;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.repository.ProcessedCartIdRepository;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.repository.OrderItemRepository;
import me.doruk.orderservice.repository.CustomerRepository;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

  private final ProcessedCartIdRepository processedCartIdRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final KafkaTemplate<String, ReserveInventory> kafkaTemplate;

  @Autowired
  public OrderService(
      final ProcessedCartIdRepository processedCartIdRepository,
      final CustomerRepository customerRepository,
      final OrderRepository orderRepository,
      final OrderItemRepository orderItemRepository,
      final KafkaTemplate<String, ReserveInventory> kafkaTemplate) {

    this.processedCartIdRepository = processedCartIdRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public List<UserResponse> GetAllUsers() {
    final List<Customer> users = customerRepository.findAll();

    return users.stream().map(user -> UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build()).toList();
  }

  public UserResponse createUser(final UserCreateRequest request) {
    System.out.println("Create user called: " + request);
    Customer customer = new Customer();
    customer.setName(request.getName());
    customer.setEmail(request.getEmail());

    Customer savedUser = customerRepository.save(customer);

    return UserResponse.builder()
        .id(savedUser.getId())
        .name(savedUser.getName())
        .email(savedUser.getEmail())
        .build();
  }

  @KafkaListener(topics = "order-requested", groupId = "order-service")
  public void orderEvent(OrderCreationRequested request) {
    log.info("Received order event: {}", request);

    // Idempotency check: skip if order for this cart already exists
    UUID cartId = request.getCartId();
    if (processedCartIdRepository.existsById(cartId)) {
      log.info("Duplicate message for cartId={}, skipping.", cartId);
      return;
    }

    System.out.println("Idempotency check passed for cartId=" + cartId);

    // Mark cart as processed
    processedCartIdRepository
        .save(ProcessedCartId.builder()
            .cartId(cartId)
            .build());

    System.out.println("Marked cartId as processed.");

    // Create or get Customer
    Customer customer = customerRepository.findByEmail(request.getEmail())
        .orElseGet(() -> {
          Customer newCustomer = Customer.builder()
              .name(request.getCustomerName())
              .email(request.getEmail())
              .build();
          customerRepository.saveAndFlush(newCustomer);
          log.info("Created new customer: {}", newCustomer);
          return newCustomer;
        });

    // Create OrderItems
    List<OrderItem> orderItems = createOrderItems(request);

    // Calculate total price
    // BigDecimal totalPrice = orderItems.stream()
    // .map(item ->
    // item.getTicketPrice().multiply(BigDecimal.valueOf(item.getTicketCount())))
    // .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Create Order object and save to db
    Order order = createOrder(customer.getId());
    orderRepository.saveAndFlush(order);

    // Add order id to each order item and save to db
    orderItems.forEach(item -> item.setOrderId(order.getId()));
    orderItemRepository.saveAllAndFlush(orderItems);

    System.out.println("Order saved with id=" + order.getId());

    // Create a list of event ids and ticket counts
    List<CartItem> reserveItems = orderItems.stream()
        .map(item -> CartItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .build())
        .toList();

    ReserveInventory reserveInventory = ReserveInventory.builder()
        .orderId(order.getId())
        .items(reserveItems)
        .build();

    System.out.println("Sending reserve inventory: " + reserveInventory);

    // Update inventory in catalog-service
    kafkaTemplate.send("reserve-inventory", reserveInventory);
  }

  private List<OrderItem> createOrderItems(OrderCreationRequested request) {
    return request.getItems().stream()
        .map(item -> OrderItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .build())
        .toList();
  }

  private Order createOrder(Long customerId) {

    return Order.builder()
        .customerId(customerId)
        .status("PENDING")
        .build();
  }

  public List<OrderResponse> getAllOrders() {
    final List<Order> orders = orderRepository.findAll();

    return orders.stream().map((Order order) -> OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build()).toList();
  }

  private List<OrderItem> getOrderItems(Long orderId) {
    return orderItemRepository.findAllByOrderId(orderId).orElse(List.of());
  }

}
