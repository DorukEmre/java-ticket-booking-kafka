package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemService {

  private final CartRedisRepository cartRedisRepository;

  // Add or update item in cart
  public ResponseEntity<Void> saveCartItem(final UUID cartId, final CartItem item) {
    System.out.println("Add item called: " + cartId + ", " + item);
    CartCacheEntry cartCache = cartRedisRepository.getCartFromRedis(cartId);
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
      cartRedisRepository.saveCartToRedis(cartId, cartCache);
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

    CartCacheEntry cartCache = cartRedisRepository.getCartFromRedis(cartId);
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
      cartRedisRepository.saveCartToRedis(cartId, cartCache);
      log.info("Deleted item from cart: {}", cartCache);
    } catch (Exception e) {
      log.error("Error interacting with Redis", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to Redis");
    }

    return ResponseEntity.noContent().build();
  }

}
