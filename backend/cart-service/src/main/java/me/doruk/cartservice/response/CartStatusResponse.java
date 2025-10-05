package me.doruk.cartservice.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.cartservice.model.CartStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartStatusResponse {
  private UUID cartId;
  private Long orderId;
  private CartStatus status;
}
