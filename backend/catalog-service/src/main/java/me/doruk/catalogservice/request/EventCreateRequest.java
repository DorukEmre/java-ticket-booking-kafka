package me.doruk.catalogservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCreateRequest {
  private String name;
  private int totalCapacity;
  private Long venueId;
  private BigDecimal ticketPrice;
  private String eventDate;
  private String description;
}
