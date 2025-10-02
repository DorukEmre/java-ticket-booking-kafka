package me.doruk.orderService.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.orderService.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
  private Long id;
  private BigDecimal totalPrice;
  private String placedAt;
  private Long customerId;
  private List<OrderItem> items;
}
