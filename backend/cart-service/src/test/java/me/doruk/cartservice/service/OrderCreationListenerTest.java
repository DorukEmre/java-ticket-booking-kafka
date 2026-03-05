package me.doruk.cartservice.service;

import me.doruk.cartservice.model.CartCacheEntry;
import me.doruk.cartservice.model.CartStatus;
import me.doruk.ticketingcommonlibrary.event.OrderCreationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderCreationListenerTest {

  private static final UUID CART_ID = UUID.fromString("8d3491ee-4759-44d3-926e-73ce9b30c0bb");
  private static final String ORDER_ID = "UOPDysF0";

  @Mock
  private CartRedisRepository cartRedisRepository;

  @InjectMocks
  private OrderCreationListener listener;

  private CartCacheEntry cart;
  private OrderCreationResponse response;

  @BeforeEach
  void setUp() {
    cart = new CartCacheEntry();

    response = new OrderCreationResponse();
    response.setCartId(CART_ID);
    response.setOrderId(ORDER_ID);
  }

  @Test
  void handleOrderCreationFailed_updatesCartStatusToFailed() {
    when(cartRedisRepository.getCartFromRedis(CART_ID)).thenReturn(cart);

    listener.handleOrderCreationFailed(response);

    assertThat(cart.getOrderId()).isEqualTo(ORDER_ID);
    assertThat(cart.getStatus()).isEqualTo(CartStatus.FAILED);

    verify(cartRedisRepository).saveCartToRedis(CART_ID, cart);
  }

  @Test
  void handleOrderCreationInvalid_updatesStatusAndItems() {
    when(cartRedisRepository.getCartFromRedis(CART_ID)).thenReturn(cart);

    listener.handleOrderCreationInvalid(response);

    assertThat(cart.getOrderId()).isEqualTo(ORDER_ID);
    assertThat(cart.getStatus()).isEqualTo(CartStatus.INVALID);
    assertThat(cart.getItems()).isEqualTo(response.getItems());

    verify(cartRedisRepository).saveCartToRedis(CART_ID, cart);
  }

  @Test
  void handleOrderCreationSucceeded_updatesStatusAndItems() {
    when(cartRedisRepository.getCartFromRedis(CART_ID)).thenReturn(cart);

    listener.handleOrderCreationSucceeded(response);

    assertThat(cart.getOrderId()).isEqualTo(ORDER_ID);
    assertThat(cart.getStatus()).isEqualTo(CartStatus.CONFIRMED);
    assertThat(cart.getItems()).isEqualTo(response.getItems());

    verify(cartRedisRepository).saveCartToRedis(CART_ID, cart);
  }

  @Test
  void shouldNotSaveCartWhenCartNotFound() {
    when(cartRedisRepository.getCartFromRedis(CART_ID)).thenReturn(null);

    listener.handleOrderCreationFailed(response);

    verify(cartRedisRepository, never()).saveCartToRedis(any(), any());
  }
}