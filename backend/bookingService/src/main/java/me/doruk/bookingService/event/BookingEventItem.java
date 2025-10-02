package me.doruk.bookingService.event;

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
public class BookingEventItem {
  private Long eventId;
  private Long ticketCount;
  private BigDecimal ticketPrice;
}
