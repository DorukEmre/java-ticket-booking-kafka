package me.doruk.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.model.CartCacheEntry;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartRedisRepository {

  private static final long CART_TTL_SECONDS = 86400; // 24 hours

  private final RedisTemplate<String, Object> redisTemplate;

  public String key(UUID cartId) {
    return "cart:" + cartId;
  }

  public CartCacheEntry getCartFromRedis(UUID cartId) {
    return (CartCacheEntry) redisTemplate.opsForValue().get(key(cartId));
  }

  public void saveCartToRedis(UUID cartId, CartCacheEntry cartCache) {
    redisTemplate.opsForValue().set(key(cartId), cartCache, CART_TTL_SECONDS, TimeUnit.SECONDS);
  }

  public void deleteCartFromRedis(UUID cartId) {
    redisTemplate.opsForValue().getAndDelete(key(cartId));
  }

}
