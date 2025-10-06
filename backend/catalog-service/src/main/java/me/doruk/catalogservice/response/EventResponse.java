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
public class EventResponse {
  private Long eventId;
  private String name;
  private int capacity;
  private Venue venue;
  private BigDecimal ticketPrice;
  private String eventDate;
  private String description;
  private String imageUrl;
}
