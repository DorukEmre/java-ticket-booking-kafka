package me.doruk.ticketingcommonlibrary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderCreationRequested {
  private UUID cartId;
  private String customerName;
  private String email;
  private List<CartItem> items;
}
