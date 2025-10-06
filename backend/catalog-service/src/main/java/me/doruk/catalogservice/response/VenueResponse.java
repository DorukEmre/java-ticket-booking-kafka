package me.doruk.catalogservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponse {
  private Long venueId;
  private String name;
  private String location;
  private int totalCapacity;
  private String imageUrl;
}
