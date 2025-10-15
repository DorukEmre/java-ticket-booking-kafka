package me.doruk.cartservice.controller;

import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartIdResponse;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.service.CartItemService;
import me.doruk.cartservice.service.CartLifecycle;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CartController {

  private final CartItemService cartItemService;
  private final CartLifecycle cartLifecycle;

  @PostMapping(value = "/cart", produces = "application/json")
  public ResponseEntity<CartIdResponse> createCart() {
    System.out.println("POST /api/v1/cart called");
    return cartLifecycle.createCart();
  }

  @GetMapping(value = "/cart/{cartId}", produces = "application/json")
  public ResponseEntity<CartResponse> getCart(@PathVariable("cartId") UUID cartId) {
    System.out.println("GET /api/v1/cart/{cartId} called");
    return cartLifecycle.getCart(cartId);
  }

  @PutMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> saveCartItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("PUT /api/v1/cart/{cartId}/items called");
    return cartItemService.saveCartItem(cartId, request);
  }

  @DeleteMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> deleteCartItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("DELETE /api/v1/cart/{cartId}/items called");
    return cartItemService.deleteCartItem(cartId, request);
  }

  @DeleteMapping(value = "/cart/{cartId}", produces = "application/json")
  public ResponseEntity<Void> deleteCart(@PathVariable("cartId") UUID cartId) {
    System.out.println("DELETE /api/v1/cart/{cartId} called");
    return cartLifecycle.deleteCart(cartId);
  }

  @PostMapping(value = "/cart/{cartId}/checkout", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> checkout(@PathVariable("cartId") UUID cartId,
      @RequestBody CheckoutRequest request) {
    System.out.println("POST /api/v1/cart/{cartId}/checkout called");
    return cartLifecycle.checkout(cartId, request);
  }

}
