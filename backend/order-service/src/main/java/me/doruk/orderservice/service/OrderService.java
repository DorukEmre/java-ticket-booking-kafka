package me.doruk.orderservice.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationResponse;
import me.doruk.ticketingcommonlibrary.model.CartItem;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.entity.OrderItem;
import me.doruk.orderservice.entity.OrderRequestLog;
import me.doruk.orderservice.model.OrderStatus;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.repository.OrderRequestLogRepository;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.repository.OrderItemRepository;
import me.doruk.orderservice.repository.CustomerRepository;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

  private final OrderRequestLogRepository orderRequestLogRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  // User methods

  public ResponseEntity<?> getOrderById(final String orderId) {
    final Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

    OrderResponse reponse = OrderResponse.builder()
        .orderId(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build();

    return ResponseEntity.ok(reponse);
  }

  public ResponseEntity<?> getAllOrdersByUser(final Long customerId) {
    customerRepository.findById(customerId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    final List<Order> orders = orderRepository.findAllByCustomerId(customerId)
        .orElse(null);

    List<OrderResponse> orderResponses = orders.stream().map((Order order) -> OrderResponse.builder()
        .orderId(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build()).toList();

    return ResponseEntity.ok(orderResponses);
  }

  public ResponseEntity<?> getAllOrdersByEmail(final String email) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return getAllOrdersByUser(customer.getId());
  }

  // Admin methods

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
        .orderId(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(getOrderItems(order.getId()))
        .build()).toList();
  }

  private List<OrderItem> getOrderItems(String orderId) {
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
            .ticketPrice(item.getTicketPrice())
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
            .processedAt(LocalDateTime.now())
            .build());

    System.out.println("cartId marked as processed.");
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

  private Order createInitialOrder(Long customerId) {

    return Order.builder()
        .id(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 8))
        .customerId(customerId)
        .status(OrderStatus.VALIDATING.name())
        .placedAt(LocalDateTime.now())
        .build();
  }

  // Listen for InventoryReservationResponse events from order-service
  @KafkaListener(topics = "inventory-reservation-failed", groupId = "order-service")
  public void handleInventoryReservationFailed(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation failed for orderId: " + request.getOrderId());

    // Update order status to INVALID
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);

    order.setStatus(OrderStatus.FAILED.name());
    orderRepository.save(order);

    log.info("Order {} marked as FAILED.", order.getId());

    kafkaTemplate.send("order-failed", OrderCreationResponse.builder()
        .orderId(order.getId())
        .cartId(orderRequestLogRepository.findByOrderId(order.getId())
            .map(OrderRequestLog::getCartId)
            .orElse(null))
        .build());

  }

  // Listen for InventoryReservationResponse events from order-service
  @KafkaListener(topics = "inventory-reservation-invalid", groupId = "order-service")
  public void handleInventoryReservationInvalid(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation invalid for orderId: " + request.getOrderId());

    // Update order status to INVALID
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);

    order.setStatus(OrderStatus.INVALID.name());
    orderRepository.save(order);

    log.info("Order {} marked as INVALID.", order.getId());

    kafkaTemplate.send("order-invalid", OrderCreationResponse.builder()
        .orderId(order.getId())
        .cartId(orderRequestLogRepository.findByOrderId(order.getId())
            .map(OrderRequestLog::getCartId)
            .orElse(null))
        .build());

  }

  // Listen for InventoryReservationResponse events from order-service
  @Transactional
  @KafkaListener(topics = "inventory-reservation-succeeded", groupId = "order-service")
  public void handleInventoryReservationSucceeded(InventoryReservationResponse request) {
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

    // Get and update Order with status and total price
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);

    BigDecimal totalPrice = orderItems.stream()
        .map(item -> item.getTicketPrice().multiply(BigDecimal.valueOf(item.getTicketCount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    order.setTotalPrice(totalPrice);
    order.setStatus(OrderStatus.PENDING_PAYMENT.name());
    orderRepository.save(order);

    log.info("Order {} saved to db as PENDING_PAYMENT.", order.getId() + ", totalPrice=" + totalPrice);

    List<CartItem> cartItems = orderItems.stream()
        .map(item -> CartItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .ticketPrice(item.getTicketPrice())
            .build())
        .toList();

    kafkaTemplate.send("order-succeeded", OrderCreationResponse.builder()
        .orderId(order.getId())
        .cartId(orderRequestLogRepository.findByOrderId(order.getId())
            .map(OrderRequestLog::getCartId)
            .orElse(null))
        .items(cartItems)
        .build());

  }

}
