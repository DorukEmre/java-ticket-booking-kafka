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
    System.out.println("Received order creation failed for orderId: " + request);

    CartCacheEntry cart = cartRedisRepository.getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.FAILED);

    cartRedisRepository.saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with failed order: {}", request.getOrderId());
  }

  // Consumer for order-invalid events from order-service
  @KafkaListener(topics = Topics.ORDER_INVALID, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationInvalid(OrderCreationResponse request) {
    System.out.println("Received order creation invalid for orderId: " + request);

    CartCacheEntry cart = cartRedisRepository.getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.INVALID);
    cart.setItems(request.getItems()); // Update items with invalid state

    cartRedisRepository.saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with invalid order: {}", request.getOrderId());
  }

  // Consumer for order-succeeded events from order-service
  @KafkaListener(topics = Topics.ORDER_SUCCEEDED, groupId = GroupIds.CART_SERVICE)
  public void handleOrderCreationSucceeded(OrderCreationResponse request) {
    System.out.println("Received order creation succeeded for orderId: " + request);

    CartCacheEntry cart = cartRedisRepository.getCartFromRedis(request.getCartId());
    if (cart == null) {
      log.error("Cart not found in Redis for cartId: {}", request.getCartId());
      return;
    }
    cart.setOrderId(request.getOrderId());
    cart.setStatus(CartStatus.CONFIRMED);
    cart.setItems(request.getItems()); // Update items with confirmed price

    cartRedisRepository.saveCartToRedis(request.getCartId(), cart);

    log.info("Updated cart in Redis with succesfull order: {}", request.getOrderId());
  }

}
