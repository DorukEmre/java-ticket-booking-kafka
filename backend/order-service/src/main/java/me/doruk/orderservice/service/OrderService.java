package me.doruk.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationFailed;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationSucceeded;
import me.doruk.ticketingcommonlibrary.event.OrderCreationFailed;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationSucceeded;
import me.doruk.ticketingcommonlibrary.model.CartItem;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.entity.OrderItem;
import me.doruk.orderservice.entity.OrderRequestLog;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.repository.OrderRequestLogRepository;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.repository.OrderItemRepository;
import me.doruk.orderservice.repository.CustomerRepository;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

  private final OrderRequestLogRepository orderRequestLogRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  public OrderService(
      final OrderRequestLogRepository orderRequestLogRepository,
      final CustomerRepository customerRepository,
      final OrderRepository orderRepository,
      final OrderItemRepository orderItemRepository,
      final KafkaTemplate<String, Object> kafkaTemplate) {

    this.orderRequestLogRepository = orderRequestLogRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public ResponseEntity<?> getOrderById(final Long orderId) {
    final Order order = orderRepository.findById(orderId).orElse(null);

    if (order == null) {
      return ResponseEntity.notFound().build();
    }

    OrderResponse reponse = OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build();

    return ResponseEntity.ok(reponse);
  }

  public ResponseEntity<?> getAllOrdersByUser(final Long customerId) {
    final List<Order> orders = orderRepository.findAllByCustomerId(customerId).orElse(List.of());

    if (orders.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    List<OrderResponse> orderResponses = orders.stream().map((Order order) -> OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build()).toList();

    return ResponseEntity.ok(orderResponses);
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

  @Transactional
  @KafkaListener(topics = "order-requested", groupId = "order-service")
  public void orderEvent(OrderCreationRequested request) {
    log.info("Received order event: {}", request);

    // Idempotency check: skip if order for this cart already exists
    UUID cartId = request.getCartId();
    if (orderRequestLogRepository.existsById(cartId)) {
      log.info("Duplicate message for cartId={}, skipping.", cartId);
      return;
    }

    System.out.println("Idempotency check passed for cartId=" + cartId);

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

    // Create Order object and save to db
    Order order = createInitialOrder(customer.getId());
    orderRepository.saveAndFlush(order);

    // Add order id to each order item and save to db
    orderItems.forEach(item -> item.setOrderId(order.getId()));
    orderItemRepository.saveAllAndFlush(orderItems);

    System.out.println("Order and items saved with id=" + order.getId());

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

    // Mark cart as processed
    orderRequestLogRepository
        .save(OrderRequestLog.builder()
            .cartId(cartId)
            .orderId(order.getId())
            .build());

    System.out.println("cartId marked as processed.");
  }

  private List<OrderItem> createOrderItems(OrderCreationRequested request) {
    return request.getItems().stream()
        .map(item -> OrderItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .build())
        .toList();
  }

  private Order createInitialOrder(Long customerId) {

    return Order.builder()
        .customerId(customerId)
        .status("PENDING")
        .build();
  }

  // Listen for InventoryReservationFailed events from order-service
  @KafkaListener(topics = "inventory-reservation-failed", groupId = "order-service")
  public void handleInventoryReservationFailed(InventoryReservationFailed request) {
    System.out.println("Received inventory reservation failed for orderId: " + request.getOrderId());

    // Update order status to FAILED
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);

    order.setStatus("FAILED");
    orderRepository.save(order);

    log.info("Order {} marked as FAILED due to inventory reservation failure.", order.getId());

    kafkaTemplate.send("order-failed", OrderCreationFailed.builder()
        .orderId(order.getId())
        .cartId(orderRequestLogRepository.findByOrderId(order.getId())
            .map(OrderRequestLog::getCartId)
            .orElse(null))
        .build());

  }

  // Listen for InventoryReservationSucceeded events from order-service
  @Transactional
  @KafkaListener(topics = "inventory-reservation-succeeded", groupId = "order-service")
  public void handleInventoryReservationSucceeded(InventoryReservationSucceeded request) {
    System.out.println("Received inventory reservation succeeded for orderId: " + request);

    // Get OrderItems
    List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(request.getOrderId()).orElse(List.of());

    System.out.println("Fetched order items: " + orderItems);

    // Update ticket prices in OrderItems
    orderItems.forEach(item -> {
      BigDecimal ticketPrice = request.getItems().stream()
          .filter(cartItem -> cartItem.getEventId().equals(item.getEventId()))
          .findFirst()
          .map(CartItem::getTicketPrice)
          .orElse(BigDecimal.ZERO);

      item.setTicketPrice(ticketPrice);
    });
    orderItemRepository.saveAll(orderItems);

    System.out.println("Updated order items ticket price: " + orderItems);

    // Calculate total price
    BigDecimal totalPrice = orderItems.stream()
        .map(item -> item.getTicketPrice().multiply(BigDecimal.valueOf(item.getTicketCount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    System.out.println("Calculated total price: " + totalPrice);

    // Update order status to CONFIRMED
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);

    order.setTotalPrice(totalPrice);
    order.setStatus("CONFIRMED");
    orderRepository.save(order);

    log.info("Order {} marked as CONFIRMED.", order.getId());

    List<CartItem> cartItems = orderItems.stream()
        .map(item -> CartItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .ticketPrice(item.getTicketPrice())
            .build())
        .toList();

    kafkaTemplate.send("order-succeeded", OrderCreationSucceeded.builder()
        .orderId(order.getId())
        .cartId(orderRequestLogRepository.findByOrderId(order.getId())
            .map(OrderRequestLog::getCartId)
            .orElse(null))
        .totalPrice(totalPrice)
        .items(cartItems)
        .build());

  }

}
