package me.doruk.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "event")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  @NotBlank(message = "Event name must not be blank")
  @Size(max = 255, message = "Event name must not exceed 255 characters")
  private String name;

  @Column(name = "total_capacity")
  @Positive(message = "Total capacity must be a positive number")
  private int totalCapacity;

  @Column(name = "remaining_capacity")
  @PositiveOrZero(message = "Remaining capacity must be zero or a positive number")
  private int remainingCapacity;

  @JoinColumn(name = "venue_id")
  @ManyToOne
  @NotNull(message = "Venue must not be null")
  private Venue venue;

  @Column(name = "ticket_price")
  @NotNull(message = "Ticket price must not be null")
  @DecimalMin(value = "0.0", inclusive = true, message = "Ticket price must be at least 0.0")
  private BigDecimal ticketPrice;

  @Column(name = "event_date")
  @NotNull(message = "Event date must not be null")
  private Date eventDate;

  @Column(name = "description")
  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;

  @Column(name = "image_url")
  @Size(max = 512, message = "Image URL must not exceed 512 characters")
  private String imageUrl;

  @PrePersist
  public void prePersist() {
    if (remainingCapacity == 0) {
      remainingCapacity = totalCapacity;
    }
  }
}
