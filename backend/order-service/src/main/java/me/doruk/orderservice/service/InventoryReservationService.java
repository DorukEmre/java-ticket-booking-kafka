package me.doruk.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.ticketingcommonlibrary.event.InventoryReleaseRequested;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCreationResponse;
import me.doruk.ticketingcommonlibrary.kafka.GroupIds;
import me.doruk.ticketingcommonlibrary.kafka.Topics;
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

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryReservationService {

  private final OrderRequestLogRepository orderRequestLogRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  // Listen for inventory-reservation-failed events from catalog-service
  @KafkaListener(topics = Topics.INVENTORY_RESERVATION_FAILED, groupId = GroupIds.ORDER_SERVICE)
  public void handleInventoryReservationFailed(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation failed for orderId: " + request.getOrderId());

    // Update order status to FAILED
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);
    if (order == null) {
      log.warn("No order found with id={}, skipping.", request.getOrderId());
      return;
    }

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
  @KafkaListener(topics = Topics.INVENTORY_RESERVATION_INVALID, groupId = GroupIds.ORDER_SERVICE)
  public void handleInventoryReservationInvalid(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation invalid for orderId: " + request.getOrderId());

    // Update order status to INVALID
    Order order = orderRepository.findById(request.getOrderId()).orElse(null);
    if (order == null) {
      log.warn("No order found with id={}, skipping.", request.getOrderId());
      return;
    }

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
  @KafkaListener(topics = Topics.INVENTORY_RESERVATION_SUCCEEDED, groupId = GroupIds.ORDER_SERVICE)
  public void handleInventoryReservationSucceeded(InventoryReservationResponse request) {
    System.out.println("Received inventory reservation succeeded for orderId: " + request);

    Order order = orderRepository.findById(request.getOrderId()).orElse(null);
    if (order == null) {
      log.warn("No order found with id={}, skipping.", request.getOrderId());
      return;
    }

    // Get OrderItems
    List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(request.getOrderId()).orElse(List.of());

    // Release inventory if order has been CANCELLED while valdating
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
}
