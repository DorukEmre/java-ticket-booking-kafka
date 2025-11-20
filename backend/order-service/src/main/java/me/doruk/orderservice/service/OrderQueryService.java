package me.doruk.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.entity.OrderItem;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.repository.OrderItemRepository;
import me.doruk.orderservice.repository.CustomerRepository;
import me.doruk.orderservice.response.OrderResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderQueryService {

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public ResponseEntity<OrderResponse> getOrderById(final String orderId) {
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

  public ResponseEntity<List<OrderResponse>> listOrdersByUserId(final Long customerId) {
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

  public ResponseEntity<List<OrderResponse>> listOrdersByEmail(final String email) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return listOrdersByUserId(customer.getId());
  }

  // Admin methods

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
}
