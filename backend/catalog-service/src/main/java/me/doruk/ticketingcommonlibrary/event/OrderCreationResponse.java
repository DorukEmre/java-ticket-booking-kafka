package me.doruk.ticketingcommonlibrary.event;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.ticketingcommonlibrary.model.CartItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationResponse {
  UUID cartId;
  String orderId;
  List<CartItem> items;
}
