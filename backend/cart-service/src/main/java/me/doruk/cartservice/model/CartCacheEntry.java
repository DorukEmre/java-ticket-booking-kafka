package me.doruk.cartservice.model;

import me.doruk.ticketingcommonlibrary.model.CartItem;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCacheEntry {
  private UUID cartId;
  private Long orderId;
  private CartStatus status;
  private List<CartItem> items;
}
