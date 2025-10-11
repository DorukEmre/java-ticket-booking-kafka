package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.client.CatalogServiceClient;
import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartIdResponse;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.CartStatusResponse;
import me.doruk.cartservice.response.InvalidCheckoutResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCancelledRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationResponse;
import me.doruk.ticketingcommonlibrary.kafka.GroupIds;
import me.doruk.ticketingcommonlibrary.kafka.Topics;
import me.doruk.ticketingcommonlibrary.model.Cart;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

  private static final long CART_TTL_SECONDS = 86400; // 24 hours

  private final CatalogServiceClient catalogServiceClient;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final RedisTemplate<String, Object> redisTemplate;

  // Redis methods

  private String key(UUID cartId) {
    return "cart:" + cartId;
  }

  private CartCacheEntry getCartFromRedis(UUID cartId) {
    return (CartCacheEntry) redisTemplate.opsForValue().get(key(cartId));
  }

  private void saveCartToRedis(UUID cartId, CartCacheEntry cartCache) {
    redisTemplate.opsForValue().set(key(cartId), cartCache, CART_TTL_SECONDS, TimeUnit.SECONDS);
  }

  private void deleteCartFromRedis(UUID cartId) {
    redisTemplate.opsForValue().getAndDelete(key(cartId));
  }

  // Get cart by ID
  public ResponseEntity<CartResponse> getCart(final UUID cartId) {
    CartCacheEntry cartCache = getCartFromRedis(cartId);
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
      saveCartToRedis(cartId, cartCache);
      log.info("Saved cart to Redis with key: {}", key(cartId));
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CartIdResponse.builder()
            .cartId(cartId)
            .build());
  }

  // Add or update item in cart
  public ResponseEntity<Void> saveCartItem(final UUID cartId, final CartItem item) {
    System.out.println("Add item called: " + cartId + ", " + item);
    CartCacheEntry cartCache = getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
    if (cartCache.getStatus() != CartStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already checked out");
    }

    if (item.getTicketCount() <= 0
        || item.getEventId() == null || item.getEventId() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid item details");
    }

    // Update item if exists, else add new item
    cartCache.getItems().stream()
        .filter(i -> i.getEventId().equals(item.getEventId()))
        .findFirst()
        .ifPresentOrElse(existing -> {
          existing.setTicketCount(item.getTicketCount());
          if (item.getTicketPrice() != null) {
            existing.setTicketPrice(item.getTicketPrice());
          }
        }, () -> cartCache.getItems().add(item));

    try {
      saveCartToRedis(cartId, cartCache);
      log.info("Updated items in cart: {}", cartCache);
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // Delete item from cart
  public ResponseEntity<Void> deleteCartItem(final UUID cartId, final CartItem item) {
    System.out.println("Delete item called: " + cartId + ", " + item);

    CartCacheEntry cartCache = getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
    if (cartCache.getStatus() != CartStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already checked out");
    }

    boolean removed = cartCache.getItems()
        .removeIf(i -> i.getEventId().equals(item.getEventId()));
    if (!removed) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart");
    }

    try {
      saveCartToRedis(cartId, cartCache);
      log.info("Deleted item from cart: {}", cartCache);
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.noContent().build();
  }

  // Delete cart
  public ResponseEntity<Void> deleteCart(final UUID cartId) {
    System.out.println("Delete cart called: " + cartId);

    CartCacheEntry cartCache = getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    try {
      deleteCartFromRedis(cartId);
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

      kafkaTemplate.send("order-cancelled", orderCancelledEvent)
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

    CartCacheEntry cartCache = getCartFromRedis(cartId);
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
    saveCartToRedis(cartId, cartCache);

    // Create order-requested event
    final OrderCreationRequested orderCreationRequested = OrderCreationRequested.builder()
        .cartId(cart.getCartId())
        .items(cart.getItems())
        .build();
    System.out.println(orderCreationRequested);

    // Send cart to order-service with Kafka
    kafkaTemplate.send("order-requested", orderCreationRequested)
        .thenAccept(result -> log.info("Cart event sent successfully: {}", orderCreationRequested))
        .exceptionally(ex -> {
          log.error("Failed to send order-requested event: {}", orderCreationRequested, ex);
          return null;
        });

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<?> checkCartStatus(final UUID cartId) {
    CartCacheEntry cart = getCartFromRedis(cartId);
    if (cart == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    CartStatusResponse statusResponse = CartStatusResponse.builder()
        .cartId(cartId)
        .orderId(cart.getOrderId())
        .status(cart.getStatus())
        .build();

    return ResponseEntity.ok(statusResponse);
  }

  // Consumer for order-failed events from order-service
  @KafkaListener(topics = Topics.ORDER_FAILED, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationFailed(OrderCreationResponse request) {
    System.out.println("Received order creation failed for orderId: " + request);

    CartCacheEntry cart = getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.FAILED);

    saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with failed order: {}", request.getOrderId());
  }

  // Consumer for order-invalid events from order-service
  @KafkaListener(topics = Topics.ORDER_INVALID, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationInvalid(OrderCreationResponse request) {
    System.out.println("Received order creation invalid for orderId: " + request);

    CartCacheEntry cart = getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.INVALID);
    cart.setItems(request.getItems()); // Update items with invalid state

    saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with invalid order: {}", request.getOrderId());
  }

  // Consumer for order-succeeded events from order-service
  @KafkaListener(topics = Topics.ORDER_SUCCEEDED, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationSucceeded(OrderCreationResponse request) {
    System.out.println("Received order creation succeeded for orderId: " + request);

    CartCacheEntry cart = getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.CONFIRMED);
    cart.setItems(request.getItems()); // Update items with confirmed price

    saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with succesfull order: {}", request.getOrderId());
  }
}
