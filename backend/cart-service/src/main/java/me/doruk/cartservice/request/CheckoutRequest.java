package me.doruk.cartservice.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
  private Long id;
  private String customerName;
  private String email;
  private List<CartRequestItem> items;
}
