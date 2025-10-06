package me.doruk.catalogservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenueCreateRequest {
  private String name;
  private String location;
  private int totalCapacity;
  private String imageUrl;
}
