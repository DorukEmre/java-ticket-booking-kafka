package me.doruk.cartservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.client.CatalogServiceClient;
import me.doruk.cartservice.model.Cart;
import me.doruk.cartservice.request.CartRequestItem;
import me.doruk.cartservice.request.CheckoutRequest;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.CatalogServiceResponse;
import me.doruk.cartservice.response.CheckoutResponse;
import me.doruk.ticketingcommonlibrary.event.OrderCreationRequested;
import me.doruk.ticketingcommonlibrary.event.CartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CartService {

  private static final long CART_TTL_SECONDS = 86400; // 24 hours

  private final CatalogServiceClient catalogServiceClient;
  private final KafkaTemplate<String, OrderCreationRequested> kafkaTemplate;
  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  public CartService(
      final CatalogServiceClient catalogServiceClient,
      final KafkaTemplate<String, OrderCreationRequested> kafkaTemplate,
      final RedisTemplate<String, Object> redisTemplate) {
    this.catalogServiceClient = catalogServiceClient;
    this.kafkaTemplate = kafkaTemplate;
    this.redisTemplate = redisTemplate;
  }

  private String key(UUID cartId) {
    return "cart:" + cartId;
  }

  public Cart getCart(UUID cartId) {
    return (Cart) redisTemplate.opsForValue().get(key(cartId));
  }

  public void saveCartToRedis(UUID cartId, Cart cart) {
    redisTemplate.opsForValue().set(key(cartId), cart, CART_TTL_SECONDS, TimeUnit.SECONDS);
  }

  public CartResponse createCart() {
    UUID cartId = UUID.randomUUID();
    System.out.println("createCart > Generated cartId: " + cartId);

    Cart cart = new Cart(cartId, new ArrayList<>());

    try {
      saveCartToRedis(cartId, cart);
      log.info("Saved cart to Redis with key: {}", key(cartId));
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return CartResponse.builder()
        .cartId(cartId)
        .message("ok")
        .build();
  }

  public CartResponse addItem(final UUID cartId, final CartItem item) {
    System.out.println("Add item called: " + cartId + ", " + item);
    Cart cart = getCart(cartId);
    if (cart == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    if (item.getTicketCount() == null || item.getTicketCount() <= 0
        || item.getEventId() == null || item.getEventId() <= 0
        || item.getTicketPrice() == null
        || item.getTicketPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid item details");
    }

    // Update item if exists, else add new item
    cart.getItems().stream()
        .filter(i -> i.getEventId().equals(item.getEventId()))
        .findFirst()
        .ifPresentOrElse(
            existing -> existing.setTicketCount(item.getTicketCount()),
            () -> cart.getItems().add(item));

    saveCartToRedis(cartId, cart);

    log.info("Updated items in cart: {}", getCart(cartId));

    return CartResponse.builder()
        .cartId(cartId)
        .message("ok")
        .build();
  }

  public CheckoutResponse checkout(final Long cartId, final CheckoutRequest request) {
    System.out.println("Create cart called: " + request);

    List<CartRequestItem> items = request.getItems();
    if (items == null || items.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart request must contain at least one item");
    }
    for (CartRequestItem item : items) {
      // check if enough catalog
      // --- get event information to also get Venue information
      final CatalogServiceResponse catalogResponse = catalogServiceClient.getCatalogService(item.getEventId());
      System.out.println(catalogResponse);

      if (catalogResponse.getCapacity() < item.getTicketCount())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough tickets available");
    }

    // create cart
    final OrderCreationRequested orderCreationRequested = createOrder(request, items);
    System.out.println(orderCreationRequested);

    // send cart to Order Service on a Kafka Topic
    kafkaTemplate.send("order-requested", orderCreationRequested)
        .thenAccept(result -> log.info("Cart event sent successfully: {}", orderCreationRequested))
        .exceptionally(ex -> {
          log.error("Failed to send order-requested event: {}", orderCreationRequested, ex);
          return null;
        });

    return CheckoutResponse.builder()
        .customerName(request.getCustomerName())
        .numberOfItems(items.size())
        .build();
  }

  private OrderCreationRequested createOrder(final CheckoutRequest request,
      final List<CartRequestItem> items) {

    return OrderCreationRequested.builder()
        .id(request.getId())
        .customerName(request.getCustomerName())
        .email(request.getEmail())
        .items(items.stream()
            .map((CartRequestItem item) -> CartItem.builder()
                .eventId(item.getEventId())
                .ticketCount(item.getTicketCount())
                .ticketPrice(item.getTicketPrice())
                .build())
            .toList())
        .build();
  }
}
