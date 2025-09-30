package me.doruk.orderService.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.bookingService.event.BookingEvent;
import me.doruk.orderService.client.InventoryServiceClient;
import me.doruk.orderService.entity.Order;
import me.doruk.orderService.repository.OrderRepository;
import me.doruk.orderService.response.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final InventoryServiceClient inventoryServiceClient;

  @Autowired
  public OrderService(final OrderRepository orderRepository,
      final InventoryServiceClient inventoryServiceClient) {
    this.orderRepository = orderRepository;
    this.inventoryServiceClient = inventoryServiceClient;
  }

  @KafkaListener(topics = "booking", groupId = "order-service")
  public void orderEvent(BookingEvent bookingEvent) {
    log.info("Received order event: {}", bookingEvent);

    // Create Order object for db
    Order order = createOrder(bookingEvent);
    orderRepository.saveAndFlush(order);

    // Update Inventory
    inventoryServiceClient.updateInventory(order.getEventId(), order.getTicketCount());
    log.info("Inventory updated for event {}, less tickets {}", order.getEventId(), order.getTicketCount());

  }

  private Order createOrder(BookingEvent bookingEvent) {
    return Order.builder()
        .totalPrice(bookingEvent.getTotalPrice())
        .ticketCount(bookingEvent.getTicketCount())
        .customerId(bookingEvent.getUserId())
        .eventId(bookingEvent.getEventId())
        .build();
  }

  public List<OrderResponse> getAllOrders() {
    final List<Order> orders = orderRepository.findAll();

    return orders.stream().map(order -> OrderResponse.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .eventId(order.getEventId())
        .ticketCount(order.getTicketCount())
        .totalPrice(order.getTotalPrice())
        .placedAt(order.getPlacedAt())
        .build()).collect(Collectors.toList());
  }

}
