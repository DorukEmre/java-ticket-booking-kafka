package me.doruk.inventory.request;

import jakarta.persistence.Column;
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
  private String address;
  private Long totalCapacity;
}
