package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.client.CatalogServiceClient;
import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.CartStatusResponse;
import me.doruk.cartservice.response.InvalidCheckoutResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCreationFailed;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.event.OrderCreationSucceeded;
import me.doruk.ticketingcommonlibrary.model.Cart;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
  private final KafkaTemplate<String, OrderCreationRequested> kafkaTemplate;
  private final RedisTemplate<String, Object> redisTemplate;

  private String key(UUID cartId) {
    return "cart:" + cartId;
  }

  public CartCacheEntry getCartFromRedis(UUID cartId) {
    return (CartCacheEntry) redisTemplate.opsForValue().get(key(cartId));
  }

  public void saveCartToRedis(UUID cartId, CartCacheEntry cartCache) {
    redisTemplate.opsForValue().set(key(cartId), cartCache, CART_TTL_SECONDS, TimeUnit.SECONDS);
  }

  // Create new cart: generate cartID and save CartCacheEntry to Redis
  public ResponseEntity<CartResponse> createCart() {
    UUID cartId = UUID.randomUUID();
    System.out.println("createCart > Generated cartId: " + cartId);

    CartCacheEntry cartCache = new CartCacheEntry(cartId, null, CartStatus.IN_PROGRESS, new ArrayList<>());

    try {
      saveCartToRedis(cartId, cartCache);
      log.info("Saved cart to Redis with key: {}", key(cartId));
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CartResponse.builder()
            .cartId(cartId)
            .build());
  }

  // Add or update item in cart
  public ResponseEntity<Void> addItem(final UUID cartId, final CartItem item) {
    System.out.println("Add item called: " + cartId + ", " + item);
    CartCacheEntry cartCache = getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    if (item.getTicketCount() <= 0
        || item.getEventId() == null || item.getEventId() <= 0
        || item.getTicketPrice() == null
        || item.getTicketPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid item details");
    }

    // Update item if exists, else add new item
    cartCache.getItems().stream()
        .filter(i -> i.getEventId().equals(item.getEventId()))
        .findFirst()
        .ifPresentOrElse(
            existing -> existing.setTicketCount(item.getTicketCount()),
            () -> cartCache.getItems().add(item));

    try {
      saveCartToRedis(cartId, cartCache);
      log.info("Updated items in cart: {}", cartCache);
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // Checkout cart (producer for order-service)
  public ResponseEntity<?> checkout(final UUID cartId, final CheckoutRequest request) {
    System.out.println("Create cart called: " + request);

    CartCacheEntry cartCache = getCartFromRedis(cartId);
    if (cartCache == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
    if (cartCache.getStatus() != CartStatus.IN_PROGRESS) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already checked out");
    }
    if (cartCache.getItems() == null || cartCache.getItems().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
    }

    // Validate items in catalog-service
    Cart cart = new Cart(cartCache.getCartId(), cartCache.getItems());
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

    // Update cart status to PENDING before sending event
    cartCache.setStatus(CartStatus.PENDING);
    saveCartToRedis(cartId, cartCache);

    // Create order-requested event
    final OrderCreationRequested orderCreationRequested = createOrderRequested(request, cartCache);
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

  private OrderCreationRequested createOrderRequested(
      final CheckoutRequest request, final CartCacheEntry cart) {

    return OrderCreationRequested.builder()
        .cartId(cart.getCartId())
        .customerName(request.getCustomerName())
        .email(request.getEmail())
        .items(cart.getItems().stream()
            .map((CartItem item) -> CartItem.builder()
                .eventId(item.getEventId())
                .ticketCount(item.getTicketCount())
                .build())
            .toList())
        .build();
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

  // Consumer for OrderCreationFailed events from order-service
  @KafkaListener(topics = "order-failed", groupId = "cart-service")
  public void handleOrderCreationFailed(OrderCreationFailed request) {
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

  // Consumer for OrderCreationSucceeded events from order-service
  @KafkaListener(topics = "order-succeeded", groupId = "cart-service")
  public void handleOrderCreationSucceeded(OrderCreationSucceeded request) {
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
