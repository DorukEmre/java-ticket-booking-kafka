package me.doruk.bookingService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BookingEvent {
  private Long id;
  private String customerName;
  private String email;
  private List<BookingEventItem> bookingEventItems;
}
