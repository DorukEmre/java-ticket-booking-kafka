package me.doruk.cartservice.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartEventItem {
  private Long eventId;
  private Long ticketCount;
  private BigDecimal ticketPrice;
}
