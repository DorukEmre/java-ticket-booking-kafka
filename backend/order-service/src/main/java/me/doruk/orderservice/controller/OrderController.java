package me.doruk.orderservice.controller;

import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;
import me.doruk.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class OrderController {

  private final OrderService orderService;

  @Autowired
  public OrderController(final OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/orders")
  public List<OrderResponse> getOrders() {
    System.out.println("GET /api/v1/orders called");
    return orderService.getAllOrders();
  }

  @GetMapping("/users")
  public @ResponseBody List<UserResponse> getUsers() {
    System.out.println("GET /api/v1/users called");
    return orderService.GetAllUsers();
  }

  @PostMapping(value = "/users/new", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
    System.out.println("POST /api/v1/users/new called");
    UserResponse createdUser = orderService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

}
