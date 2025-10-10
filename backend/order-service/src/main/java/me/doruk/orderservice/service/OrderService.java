package me.doruk.orderservice.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.event.InventoryReleaseRequested;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCancelledRequested;
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
import me.doruk.orderservice.request.PaymentRequest;
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
        .items(listOrderItems(order.getId()))
        .build();

    return ResponseEntity.ok(reponse);
  }

  public ResponseEntity<?> listOrdersByUserId(final Long customerId) {
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
        .items(listOrderItems(order.getId()))
        .build()).toList();

    return ResponseEntity.ok(orderResponses);
  }

  public ResponseEntity<?> listOrdersByEmail(final String email) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return listOrdersByUserId(customer.getId());
  }

  // Admin methods

  public List<UserResponse> listAllUsers() {
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

  public List<OrderResponse> listAllOrders() {
    final List<Order> orders = orderRepository.findAll();

    return orders.stream().map((Order order) -> OrderResponse.builder()
        .orderId(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .status(order.getStatus())
        .items(listOrderItems(order.getId()))
        .build()).toList();
  }

  private List<OrderItem> listOrderItems(String orderId) {
    return orderItemRepository.findAllByOrderId(orderId).orElse(List.of());
  }

  // Listen for order-requested events from cart-service
  @Transactional
  @KafkaListener(topics = "order-requested", groupId = "order-service")
  public void handleOrderCreationRequested(OrderCreationRequested request) {
    log.info("Received order event: {}", request);

    // Idempotency check: skip if order for this cart already exists
    UUID cartId = request.getCartId();
    if (orderRequestLogRepository.existsById(cartId)) {
      log.info("Duplicate message for cartId={}, skipping.", cartId);
      return;
    }

    System.out.println("Idempotency check passed for cartId=" + cartId);

    // Create OrderItems
    List<OrderItem> orderItems = createOrderItems(request);

    // Create Order object and save to db
    Order order = createInitialOrder();
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

  private Order createInitialOrder() {

    return Order.builder()
        .id(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 8))
        .status(OrderStatus.VALIDATING.name())
        .placedAt(LocalDateTime.now())
        .build();
  }

  public ResponseEntity<?> processPayment(final String orderId, final PaymentRequest request) {

    // Check order is PENDING_PAYMENT
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

    if (!order.getStatus().equals(OrderStatus.PENDING_PAYMENT.name())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not pending payment");
    }

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

    // Update order status to COMPLETED and link Customer
    order.setStatus(OrderStatus.COMPLETED.name());
    order.setCustomerId(customer.getId());
    orderRepository.save(order);

    log.info("Order {} marked as COMPLETED.", order.getId());

    return ResponseEntity.ok().build();
  }

  // Listen for inventory-reservation-failed events from catalog-service
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

  // Listen for inventory-reservation-invalid events from catalog-service
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

  // Listen for inventory-reservation-succeeded events from catalog-service
  @Transactional
  @KafkaListener(topics = "inventory-reservation-succeeded", groupId = "order-service")
  public void handleInventoryReservationSucceeded(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation succeeded for orderId: " + request);

    Order order = orderRepository.findById(request.getOrderId()).orElse(null);
    if (order == null) {
      log.warn("No order found with id={}, skipping.", request.getOrderId());
      return;
    }

    // Get OrderItems
    List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(request.getOrderId()).orElse(List.of());

    // Release inventory if order is already CANCELLED
    if (order.getStatus().equals(OrderStatus.CANCELLED.name())) {
      log.info("Order {} is CANCELLED, release inventory required.", order.getId());

      // Send event to catalog-service to release inventory
      kafkaTemplate.send("release-inventory",
          InventoryReleaseRequested.builder()
              .orderId(order.getId())
              .items(orderItems.stream().map(item -> CartItem.builder()
                  .eventId(item.getEventId())
                  .ticketCount(item.getTicketCount())
                  .ticketPrice(item.getTicketPrice())
                  .build()).toList())
              .build());

      return;
    }

    // Skip if order is not in VALIDATING status
    if (!order.getStatus().equals(OrderStatus.VALIDATING.name())) {
      log.info("Order {} is not VALIDATING, skipping.", order.getId());
      return;
    }

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

    // Update Order status and total price
    BigDecimal totalPrice = orderItems.stream()
        .map(item -> item.getTicketPrice().multiply(BigDecimal.valueOf(item.getTicketCount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    order.setTotalPrice(totalPrice);
    order.setStatus(OrderStatus.PENDING_PAYMENT.name());
    orderRepository.save(order);

    log.info("Order {} saved to db as PENDING_PAYMENT.", order.getId() + ", totalPrice=" + totalPrice, orderItems);

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

  // Listen for order-cancelled from cart-service
  @Transactional
  @KafkaListener(topics = "order-cancelled", groupId = "order-service")
  public void handleOrderCancelled(OrderCancelledRequested request) {
    System.out.println("Received order cancelled for cartId: " + request.getCartId());

    // Get Order by cartId
    OrderRequestLog orderRequestLog = orderRequestLogRepository.findById(request.getCartId()).orElse(null);
    if (orderRequestLog == null) {
      log.warn("No order found for cartId={}, skipping cancellation.",
          request.getCartId());
      return;
    }

    Order order = orderRepository.findById(orderRequestLog.getOrderId())
        .orElse(null);
    if (order == null) {
      log.warn("No order found with id={}, skipping cancellation.",
          orderRequestLog.getOrderId());
      return;
    }

    if (!order.getStatus().equals(OrderStatus.VALIDATING.name())
        && !order.getStatus().equals(OrderStatus.INVALID.name())
        && !order.getStatus().equals(OrderStatus.PENDING_PAYMENT.name())) {
      log.info("Order {} is not VALIDATING, INVALID or PENDING_PAYMENT, skipping.", order.getId());
      return;
    }

    String previousStatus = order.getStatus();

    // Update order status to CANCELLED
    order.setStatus(OrderStatus.CANCELLED.name());
    orderRepository.save(order);
    log.info("Order {} marked as CANCELLED.", order.getId());

    // If PENDING_PAYMENT, send event to catalog-service to release inventory
    if (previousStatus.equals(OrderStatus.PENDING_PAYMENT.name())) {

      List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId()).orElse(List.of());

      kafkaTemplate.send("release-inventory",
          InventoryReleaseRequested.builder()
              .orderId(order.getId())
              .items(orderItems.stream().map(item -> CartItem.builder()
                  .eventId(item.getEventId())
                  .ticketCount(item.getTicketCount())
                  .ticketPrice(item.getTicketPrice())
                  .build()).toList())
              .build());
      log.info("Sent release-inventory event for order {}.", order.getId());
    }

  }
}
