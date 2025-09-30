package me.doruk.orderService.controller;

import me.doruk.orderService.response.OrderResponse;
import me.doruk.orderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
