package me.doruk.cartservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogServiceResponse {
  private Long eventId;
  private String event;
  private int capacity;
  private VenueResponse venue;
  private BigDecimal ticketPrice;
}
