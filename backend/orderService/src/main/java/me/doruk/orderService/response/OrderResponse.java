package me.doruk.orderService.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
  private Long id;
  private BigDecimal totalPrice;
  private Long ticketCount;
  private String placedAt;
  private Long customerId;
  private Long eventId;
}
