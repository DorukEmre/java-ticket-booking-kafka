package me.doruk.cartservice.controller;

import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartIdResponse;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.InvalidCheckoutResponse;
import me.doruk.cartservice.service.CartItemService;
import me.doruk.cartservice.service.CartLifecycle;
import me.doruk.ticketingcommonlibrary.model.ApiErrorResponse;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CartController {

  private final CartItemService cartItemService;
  private final CartLifecycle cartLifecycle;

  @Operation(summary = "Create a new cart", description = "Creates a new shopping cart and returns its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cart created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartIdResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @PostMapping(value = "/cart", produces = "application/json")
  public ResponseEntity<CartIdResponse> createCart() {
    System.out.println("POST /api/v1/cart called");
    return cartLifecycle.createCart();
  }

  @Operation(summary = "Get cart", description = "Retrieves the shopping cart by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponse.class))),
      @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping(value = "/cart/{cartId}", produces = "application/json")
  public ResponseEntity<CartResponse> getCart(@PathVariable("cartId") UUID cartId) {
    System.out.println("GET /api/v1/cart/{cartId} called");
    return cartLifecycle.getCart(cartId);
  }

  @Operation(summary = "Add or update item in cart", description = "Adds a new item into the shopping cart or updates an existing item's quantity.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Item added/updated successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Cart already checked out or invalid item details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Unable to connect to Redis", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @PutMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> saveCartItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("PUT /api/v1/cart/{cartId}/items called");
    return cartItemService.saveCartItem(cartId, request);
  }

  @Operation(summary = "Delete item from cart", description = "Deletes a specific item from the shopping cart.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted item from cart successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Cart already checked out", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Cart not found or item not found in cart", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Unable to connect to Redis", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @DeleteMapping(value = "/cart/{cartId}/items", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> deleteCartItem(@PathVariable("cartId") UUID cartId, @RequestBody CartItem request) {
    System.out.println("DELETE /api/v1/cart/{cartId}/items called");
    return cartItemService.deleteCartItem(cartId, request);
  }

  @Operation(summary = "Delete cart", description = "Deletes the specified shopping cart and all its items.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cart deleted successfully", content = @Content),
      @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Unable to connect to Redis", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @DeleteMapping(value = "/cart/{cartId}", produces = "application/json")
  public ResponseEntity<Void> deleteCart(@PathVariable("cartId") UUID cartId) {
    System.out.println("DELETE /api/v1/cart/{cartId} called");
    return cartLifecycle.deleteCart(cartId);
  }

  @Operation(summary = "Checkout cart", description = "Initiates the checkout process for the specified cart. Validates cart contents and triggers downstream order request.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Checkout accepted and is being processed", content = @Content),
      @ApiResponse(responseCode = "400", description = "Cart empty, cart already checked out, or invalid items in cart (response body contains invalidItemIds).", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidCheckoutResponse.class))),
      @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Unable to connect to Redis", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @PostMapping(value = "/cart/{cartId}/checkout", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> checkout(@PathVariable("cartId") UUID cartId,
      @RequestBody CheckoutRequest request) {
    System.out.println("POST /api/v1/cart/{cartId}/checkout called");
    return cartLifecycle.checkout(cartId, request);
  }

}
