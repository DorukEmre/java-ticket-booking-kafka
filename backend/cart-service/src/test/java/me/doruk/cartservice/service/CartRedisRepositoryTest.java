package me.doruk.cartservice.service;

import me.doruk.cartservice.model.CartCacheEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartRedisRepositoryTest {

  private static final UUID CART_ID = UUID.fromString("8d3491ee-4759-44d3-926e-73ce9b30c0bb");

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ValueOperations<String, Object> valueOperations;

  @InjectMocks
  private CartRedisRepository repository;

  @BeforeEach
  void setup() {
    // Stub opsForValue to return mock ValueOperations for unit testing
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  void getCartFromRedis_shouldCallRedis() {
    CartCacheEntry cart = new CartCacheEntry();
    when(valueOperations.get("cart:" + CART_ID)).thenReturn(cart);

    CartCacheEntry result = repository.getCartFromRedis(CART_ID);

    assertThat(result).isEqualTo(cart);
    verify(valueOperations).get("cart:" + CART_ID);
  }

  @Test
  void saveCartToRedis_shouldStoreWithTTL() {
    CartCacheEntry cart = new CartCacheEntry();

    repository.saveCartToRedis(CART_ID, cart);

    verify(valueOperations).set(
        eq("cart:" + CART_ID),
        eq(cart),
        eq(86400L),
        eq(TimeUnit.SECONDS));
  }

  @Test
  void deleteCartFromRedis_shouldCallDelete() {
    repository.deleteCartFromRedis(CART_ID);

    verify(valueOperations).getAndDelete("cart:" + CART_ID);
  }
}