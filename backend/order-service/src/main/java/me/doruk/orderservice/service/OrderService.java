package me.doruk.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.orderservice.client.CatalogServiceClient;
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
  private final CatalogServiceClient catalogServiceClient;

  @Autowired
  public OrderService(
      final ProcessedCartIdRepository processedCartIdRepository,
      final CustomerRepository customerRepository,
      final OrderRepository orderRepository,
      final OrderItemRepository orderItemRepository,
      final CatalogServiceClient catalogServiceClient) {

    this.processedCartIdRepository = processedCartIdRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.catalogServiceClient = catalogServiceClient;
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

    // Mark cart as processed
    processedCartIdRepository
        .save(ProcessedCartId.builder()
            .cartId(cartId)
            .build());

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
    BigDecimal totalPrice = orderItems.stream()
        .map(item -> item.getTicketPrice().multiply(BigDecimal.valueOf(item.getTicketCount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Create Order object and save to db
    Order order = createOrder(customer.getId(), totalPrice);
    orderRepository.saveAndFlush(order);

    // Add order id to each order item and save to db
    orderItems.forEach(item -> item.setOrderId(order.getId()));
    orderItemRepository.saveAllAndFlush(orderItems);

    // Create a list of event ids and ticket counts
    List<ReserveInventory> eventTicketCounts = orderItems.stream()
        .map(item -> ReserveInventory.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .build())
        .toList();

    // Update remaining ticket in CatalogService
    catalogServiceClient.updateCatalogService(eventTicketCounts);

    // TO DO: Delete cart from Redis.

    // TO DO: Return orderId and order details.

  }

  private List<OrderItem> createOrderItems(OrderCreationRequested request) {
    return request.getItems().stream()
        .map(item -> OrderItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .ticketPrice(item.getTicketPrice())
            .build())
        .toList();
  }

  private Order createOrder(Long customerId, BigDecimal totalPrice) {

    return Order.builder()
        .customerId(customerId)
        .totalPrice(totalPrice)
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
