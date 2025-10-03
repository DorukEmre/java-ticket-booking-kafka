package me.doruk.ticketingcommonlibrary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationRequested {
  Long eventId;
  Long ticketCount;
}
