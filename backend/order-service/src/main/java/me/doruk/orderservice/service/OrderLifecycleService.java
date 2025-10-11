package me.doruk.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.kafka.GroupIds;
import me.doruk.ticketingcommonlibrary.kafka.Topics;
import me.doruk.ticketingcommonlibrary.event.InventoryReleaseRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCancelledRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.model.CartItem;
import me.doruk.orderservice.entity.OrderItem;
import me.doruk.orderservice.entity.OrderRequestLog;
import me.doruk.orderservice.model.OrderStatus;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.repository.OrderRequestLogRepository;
import me.doruk.orderservice.repository.OrderItemRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderLifecycleService {

  private final OrderRequestLogRepository orderRequestLogRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  // Listen for order-requested events from cart-service
  @Transactional
  @KafkaListener(topics = Topics.ORDER_REQUESTED, groupId = GroupIds.ORDER_SERVICE)
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
        .id(NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(),
            8))
        .status(OrderStatus.VALIDATING.name())
        .placedAt(LocalDateTime.now())
        .build();
  }

  // Listen for order-cancelled from cart-service
  @Transactional
  @KafkaListener(topics = Topics.ORDER_CANCELLED, groupId = GroupIds.ORDER_SERVICE)
  public void handleOrderCancelled(OrderCancelledRequested request) {
    log.info("Received order cancelled for cartId: " + request.getCartId());

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
