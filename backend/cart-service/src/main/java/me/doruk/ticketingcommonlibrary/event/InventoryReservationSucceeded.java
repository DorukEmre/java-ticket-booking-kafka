package me.doruk.ticketingcommonlibrary.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.ticketingcommonlibrary.model.CartItem;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationSucceeded {
  Long orderId;
  List<CartItem> items;
}
