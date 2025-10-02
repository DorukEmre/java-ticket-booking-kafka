package me.doruk.catalogservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueCatalogServiceResponse {
  private Long venueId;
  private String name;
  private String address;
  private Long totalCapacity;
}
