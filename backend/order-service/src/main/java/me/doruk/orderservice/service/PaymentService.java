package me.doruk.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.model.OrderStatus;
import me.doruk.orderservice.entity.Order;
import me.doruk.orderservice.repository.OrderRepository;
import me.doruk.orderservice.request.PaymentRequest;
import me.doruk.orderservice.repository.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;

  public ResponseEntity<Void> processPayment(final String orderId, final PaymentRequest request) {

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

}
