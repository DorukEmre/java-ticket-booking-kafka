package me.doruk.orderService.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.bookingService.event.BookingEvent;
import me.doruk.orderService.client.InventoryServiceClient;
import me.doruk.orderService.dto.TicketCountForEvent;
import me.doruk.orderService.entity.Customer;
import me.doruk.orderService.entity.OrderItem;
import me.doruk.orderService.entity.Order;
import me.doruk.orderService.repository.OrderRepository;
import me.doruk.orderService.request.UserCreateRequest;
import me.doruk.orderService.repository.OrderItemRepository;
import me.doruk.orderService.repository.CustomerRepository;
import me.doruk.orderService.response.OrderResponse;
import me.doruk.orderService.response.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

  private final CustomerRepository customerRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderRepository orderRepository;
  private final InventoryServiceClient inventoryServiceClient;

  @Autowired
  public OrderService(
      final CustomerRepository customerRepository,
      final OrderItemRepository orderItemRepository,
      final OrderRepository orderRepository,
      final InventoryServiceClient inventoryServiceClient) {

    this.orderRepository = orderRepository;
    this.inventoryServiceClient = inventoryServiceClient;
    this.customerRepository = customerRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public List<UserResponse> GetAllUsers() {
    final List<Customer> users = customerRepository.findAll();

    return users.stream().map(user -> UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build()).collect(Collectors.toList());
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

  @KafkaListener(topics = "booking", groupId = "order-service")
  public void orderEvent(BookingEvent bookingEvent) {
    log.info("Received order event: {}", bookingEvent);

    // TO DO: Validate cart exists in Redis
    // TO DO: Idempotency check: skip if order for this event already exists

    // Create or get Customer
    Customer customer = customerRepository.findByEmail(bookingEvent.getEmail())
        .orElseGet(() -> {
          Customer newCustomer = Customer.builder()
              .name(bookingEvent.getCustomerName())
              .email(bookingEvent.getEmail())
              .build();
          customerRepository.saveAndFlush(newCustomer);
          log.info("Created new customer: {}", newCustomer);
          return newCustomer;
        });

    // Create OrderItems
    List<OrderItem> orderItems = createOrderItems(bookingEvent);

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
    List<TicketCountForEvent> eventTicketCounts = orderItems.stream()
        .map(item -> TicketCountForEvent.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .build())
        .collect(Collectors.toList());

    // Update remaining ticket in Inventory
    inventoryServiceClient.updateInventory(eventTicketCounts);

    // TO DO: Delete cart from Redis.

    // TO DO: Return orderId and order details.

  }

  private List<OrderItem> createOrderItems(BookingEvent bookingEvent) {
    return bookingEvent.getBookingEventItems().stream()
        .map(item -> OrderItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .ticketPrice(item.getTicketPrice())
            .build())
        .collect(Collectors.toList());
  }

  private Order createOrder(Long customerId, BigDecimal totalPrice) {

    return Order.builder()
        .customerId(customerId)
        .totalPrice(totalPrice)
        .build();
  }

  public List<OrderResponse> getAllOrders() {
    final List<Order> orders = orderRepository.findAll();

    return orders.stream().map((Order order) -> OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .items(getOrderItems(order.getId()))
        .build()).collect(Collectors.toList());
  }

  private List<OrderItem> getOrderItems(Long orderId) {
    return orderItemRepository.findAllByOrderId(orderId).orElse(List.of());
  }

}
