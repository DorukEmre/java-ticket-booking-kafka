package me.doruk.bookingService.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {
  private Long id;
  private String customerName;
  private String email;
  private List<BookingRequestItem> items;
}
