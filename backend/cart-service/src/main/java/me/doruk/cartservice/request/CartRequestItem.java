package me.doruk.cartservice.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartRequestItem {
  private Long eventId;
  private Long ticketCount;
  private BigDecimal ticketPrice;
}
