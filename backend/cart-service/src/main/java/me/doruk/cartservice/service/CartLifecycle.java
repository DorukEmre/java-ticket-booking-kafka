package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.client.CatalogServiceClient;
import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartIdResponse;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.InvalidCheckoutResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCancelledRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.kafka.Topics;
import me.doruk.ticketingcommonlibrary.model.Cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartLifecycle {

  private final CatalogServiceClient catalogServiceClient;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final CartRedisRepository cartRedisRepository;

  // Get cart by ID
  public ResponseEntity<CartResponse> getCart(final UUID cartId) {
    CartCacheEntry cartCache = cartRedisRepository.getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
    return ResponseEntity.ok(CartResponse.builder()
        .cartId(cartCache.getCartId())
        .orderId(cartCache.getOrderId())
        .status(cartCache.getStatus())
        .items(cartCache.getItems())
        .build());
  }

  // Create new cart: generate cartID and save CartCacheEntry to Redis
  public ResponseEntity<CartIdResponse> createCart() {
    UUID cartId = UUID.randomUUID();
    System.out.println("createCart > Generated cartId: " + cartId);

    CartCacheEntry cartCache = new CartCacheEntry(
        cartId, null, CartStatus.PENDING, new ArrayList<>());

    try {
      cartRedisRepository.saveCartToRedis(cartId, cartCache);
      log.info("Saved cart to Redis with key: {}", cartRedisRepository.key(cartId));
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CartIdResponse.builder()
            .cartId(cartId)
            .build());
  }

  // Delete cart
  public ResponseEntity<Void> deleteCart(final UUID cartId) {
    System.out.println("Delete cart called: " + cartId);

    CartCacheEntry cartCache = cartRedisRepository.getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    try {
      cartRedisRepository.deleteCartFromRedis(cartId);
      log.info("Deleted cart: {}", cartId);
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    System.out.println("Cart deleted from Redis: " + cartId + ", status: " + cartCache.getStatus() +
        ", orderId: " + cartCache.getOrderId());

    // Mark order as cancelled if exists
    if (cartCache.getOrderId() != null) {
      final OrderCancelledRequested orderCancelledEvent = OrderCancelledRequested.builder()
          .cartId(cartCache.getCartId())
          .orderId(cartCache.getOrderId())
          .build();

      kafkaTemplate.send(Topics.ORDER_CANCELLED, orderCancelledEvent)
          .thenAccept(result -> log.info("Order cancelled event sent successfully: {}",
              orderCancelledEvent))
          .exceptionally(ex -> {
            log.error("Failed to send order-cancelled event: {}", orderCancelledEvent,
                ex);
            return null;
          });
    }

    return ResponseEntity.noContent().build();
  }

  // Checkout cart (producer for order-service)
  public ResponseEntity<?> checkout(final UUID cartId, final CheckoutRequest request) {
    System.out.println("Checkout called: " + request);

    CartCacheEntry cartCache = cartRedisRepository.getCartFromRedis(cartId);
    System.out.println("Cart from Redis: " + cartCache);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
    if (cartCache.getStatus() != CartStatus.PENDING
        && cartCache.getStatus() != CartStatus.INVALID) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already checked out");
    }
    if ((cartCache.getItems() == null || cartCache.getItems().isEmpty())
        && (request.getItems() == null || request.getItems().isEmpty())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
    }

    // Validate items in catalog-service
    Cart cart = new Cart(cartCache.getCartId(), request.getItems());
    Map<Long, Boolean> itemsValidity = catalogServiceClient.validateCart(cart);

    boolean allValid = itemsValidity.values().stream().allMatch(Boolean::booleanValue);
    System.out.println("Items validity: " + itemsValidity);

    if (!allValid) {
      List<Long> invalidItemIds = itemsValidity.entrySet().stream()
          .filter(entry -> !entry.getValue())
          .map(Map.Entry::getKey)
          .toList();

      log.warn("Invalid items found during checkout: {}", invalidItemIds);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(InvalidCheckoutResponse.builder().invalidItemIds(invalidItemIds)
              .build());
    }

    // Update cart items and cart status to IN_PROGRESS before sending event
    cartCache.setItems(request.getItems());
    cartCache.setStatus(CartStatus.IN_PROGRESS);
    cartRedisRepository.saveCartToRedis(cartId, cartCache);

    // Create order-requested event
    final OrderCreationRequested orderCreationRequested = OrderCreationRequested.builder()
        .cartId(cart.getCartId())
        .items(cart.getItems())
        .build();
    System.out.println(orderCreationRequested);

    // Send cart to order-service with Kafka
    kafkaTemplate.send(Topics.ORDER_REQUESTED, orderCreationRequested)
        .thenAccept(result -> log.info("Cart event sent successfully: {}", orderCreationRequested))
        .exceptionally(ex -> {
          log.error("Failed to send order-requested event: {}", orderCreationRequested, ex);
          return null;
        });

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

}
