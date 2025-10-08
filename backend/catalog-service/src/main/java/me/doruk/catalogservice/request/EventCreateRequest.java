package me.doruk.catalogservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCreateRequest {
  @NotBlank
  @Size(max = 255)
  private String name;

  @Positive
  private int totalCapacity;

  @NotNull
  private Long venueId;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal ticketPrice;

  @NotNull
  @FutureOrPresent
  private LocalDate eventDate;

  @Size(max = 1000)
  private String description;

  @Size(max = 512)
  private String imageUrl;
}
