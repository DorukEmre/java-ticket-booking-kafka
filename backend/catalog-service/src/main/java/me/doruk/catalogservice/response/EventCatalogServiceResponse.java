package me.doruk.catalogservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.catalogservice.entity.Venue;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCatalogServiceResponse {
  private Long eventId;
  private String event;
  private Long capacity;
  private Venue venue;
  private BigDecimal ticketPrice;
  private String eventDate;
  private String description;
}
