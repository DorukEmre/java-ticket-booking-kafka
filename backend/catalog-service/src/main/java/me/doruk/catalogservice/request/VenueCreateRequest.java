package me.doruk.catalogservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenueCreateRequest {
  @NotBlank
  @Size(max = 255)
  private String name;

  @NotBlank
  @Size(max = 255)
  private String location;

  @Min(1)
  private int totalCapacity;

  @Size(max = 512)
  private String imageUrl;
}
