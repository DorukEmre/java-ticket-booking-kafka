package me.doruk.cartservice.controller;

import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.CheckoutResponse;
import me.doruk.cartservice.response.ItemResponse;
import me.doruk.cartservice.service.CartService;
import me.doruk.ticketingcommonlibrary.event.CartItem;

import java.util.UUID;

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

  @PostMapping(value = "/cart", produces = "application/json")
  public CartResponse createCart() {
    System.out.println("POST /api/v1/cart called");
    return cartService.createCart();
  }

  @PostMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ItemResponse addItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("POST /api/v1/cart/{cartId}/items called");
    return cartService.addItem(cartId, request);
  }

  @PostMapping(value = "/cart/{cartId}/checkout", consumes = "application/json", produces = "application/json")
  public CheckoutResponse checkout(@PathVariable("cartId") Long cartId, @RequestBody CheckoutRequest request) {
    System.out.println("POST /api/v1/cart/{cartId}/checkout called");
    return cartService.checkout(cartId, request);
  }
}
