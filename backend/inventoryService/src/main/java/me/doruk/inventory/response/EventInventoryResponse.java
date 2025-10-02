package me.doruk.inventory.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.doruk.inventory.entity.Venue;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventInventoryResponse {
  private Long eventId;
  private String event;
  private Long capacity;
  private Venue venue;
  private BigDecimal ticketPrice;
  private String eventDate;
  private String description;
}
