package me.doruk.cartservice.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.ticketingcommonlibrary.model.CartItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
  private Long id;
  private String customerName;
  private String email;
  private List<CartItem> items;
}
