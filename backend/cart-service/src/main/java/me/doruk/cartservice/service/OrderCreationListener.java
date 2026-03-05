package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.ticketingcommonlibrary.event.OrderCreationResponse;
import me.doruk.ticketingcommonlibrary.kafka.GroupIds;
import me.doruk.ticketingcommonlibrary.kafka.Topics;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCreationListener {

  private final CartRedisRepository cartRedisRepository;

  // Consumer for order-failed events from order-service
  @KafkaListener(topics = Topics.ORDER_FAILED, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationFailed(OrderCreationResponse request) {
    updateCart(request, CartStatus.FAILED, false);
  }

  // Consumer for order-invalid events from order-service
  @KafkaListener(topics = Topics.ORDER_INVALID, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationInvalid(OrderCreationResponse request) {
    updateCart(request, CartStatus.INVALID, true);
  }

  // Consumer for order-succeeded events from order-service
  @KafkaListener(topics = Topics.ORDER_SUCCEEDED, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationSucceeded(OrderCreationResponse request) {
    updateCart(request, CartStatus.CONFIRMED, true);
  }

  // Updates the cart in redis
  private void updateCart(OrderCreationResponse request, CartStatus status, boolean updateItems) {
    log.info("Received order event for orderId: {}", request.getOrderId());

    CartCacheEntry cart = cartRedisRepository.getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }

    cart.setOrderId(request.getOrderId());
    cart.setStatus(status);

    if (updateItems) {
      cart.setItems(request.getItems());
    }

    cartRedisRepository.saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with status {} for order {}", status, request.getOrderId());
  }

}
