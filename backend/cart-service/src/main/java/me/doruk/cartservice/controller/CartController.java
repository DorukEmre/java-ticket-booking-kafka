package me.doruk.cartservice.controller;

import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.service.CartService;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class CartController {

  private final CartService cartService;

  @Autowired
  public CartController(final CartService cartService) {
    this.cartService = cartService;
  }

  @PostMapping(value = "/cart", produces = "application/json")
  public ResponseEntity<CartResponse> createCart() {
    System.out.println("POST /api/v1/cart called");
    return cartService.createCart();
  }

  @PostMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> addItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("POST /api/v1/cart/{cartId}/items called");
    return cartService.addItem(cartId, request);
  }

  @PostMapping(value = "/cart/{cartId}/checkout", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> checkout(@PathVariable("cartId") UUID cartId,
      @RequestBody CheckoutRequest request) {
    System.out.println("POST /api/v1/cart/{cartId}/checkout called");
    return cartService.checkout(cartId, request);
  }
}
