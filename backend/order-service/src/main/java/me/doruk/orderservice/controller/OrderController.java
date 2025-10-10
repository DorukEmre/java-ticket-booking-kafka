package me.doruk.orderservice.controller;

import me.doruk.orderservice.request.PaymentRequest;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;
import me.doruk.orderservice.service.OrderQueryService;
import me.doruk.orderservice.service.PaymentService;
import me.doruk.orderservice.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class OrderController {

  private final OrderQueryService orderQueryService;
  private final PaymentService paymentService;
  private final UserService userService;

  @GetMapping("/orders/{orderId}")
  public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId) {
    System.out.println("GET /api/v1/orders/{orderId} called");
    return orderQueryService.getOrderById(orderId);
  }

  @GetMapping("/users/id/{customerId}/orders")
  public ResponseEntity<?> listOrdersByUserId(@PathVariable("customerId") Long customerId) {
    System.out.println("GET /api/v1/users/{customerId}/orders called");
    return orderQueryService.listOrdersByUserId(customerId);
  }

  @GetMapping("/users/email/{email}/orders")
  public ResponseEntity<?> listOrdersByEmail(@PathVariable("email") @Email String email) {
    System.out.println("GET /api/v1/users/{email}/orders called");
    return orderQueryService.listOrdersByEmail(email);
  }

  @PostMapping(value = "/orders/{orderId}/payment", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> processPayment(@PathVariable("orderId") String orderId,
      @RequestBody PaymentRequest request) {
    System.out.println("POST /api/v1/orders/{orderId}/payment called");
    return paymentService.processPayment(orderId, request);
  }

  // Admin routes

  @GetMapping("/orders")
  public List<OrderResponse> getOrders() {
    System.out.println("GET /api/v1/orders called");
    return orderQueryService.listAllOrders();
  }

  @GetMapping("/users")
  public List<UserResponse> getUsers() {
    System.out.println("GET /api/v1/users called");
    return userService.listAllUsers();
  }

  @PostMapping(value = "/users/new", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
    System.out.println("POST /api/v1/users/new called");
    UserResponse createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

}
