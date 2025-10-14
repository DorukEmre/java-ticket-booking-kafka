package me.doruk.ticketingcommonlibrary.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CartItem {
  private Long eventId;
  private int ticketCount;
  private BigDecimal ticketPrice;

  private BigDecimal previousPrice;
  private boolean priceChanged;
  private boolean unavailable;
}
