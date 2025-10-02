package me.doruk.cartservice.controller;

import me.doruk.cartservice.request.CartRequest;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class CartController {

  private final CartService cartService;

  @Autowired
  public CartController(final CartService cartService) {
    this.cartService = cartService;
  }

  @PostMapping(value = "/cart", consumes = "application/json", produces = "application/json")
  public CartResponse createCart(@RequestBody CartRequest request) {
    System.out.println("POST /api/v1/cart called");
    return cartService.createCart(request);
  }
}
