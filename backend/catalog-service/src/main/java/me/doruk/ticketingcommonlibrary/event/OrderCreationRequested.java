package me.doruk.ticketingcommonlibrary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderCreationRequested {
  private Long id;
  private String customerName;
  private String email;
  private List<CartItem> items;
}
