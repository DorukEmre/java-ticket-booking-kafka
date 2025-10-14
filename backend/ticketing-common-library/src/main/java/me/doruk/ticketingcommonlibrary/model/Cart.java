package me.doruk.ticketingcommonlibrary.model;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
  private UUID cartId;
  private List<CartItem> items;
}
