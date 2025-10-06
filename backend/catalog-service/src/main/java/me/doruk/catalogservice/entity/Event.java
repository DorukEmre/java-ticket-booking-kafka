package me.doruk.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;

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
  private String name;

  @Column(name = "total_capacity")
  private int totalCapacity;

  @Column(name = "remaining_capacity")
  private int remainingCapacity;

  @ManyToOne
  @JoinColumn(name = "venue_id")
  private Venue venue;

  @Column(name = "ticket_price")
  private BigDecimal ticketPrice;

  @Column(name = "event_date")
  private Date eventDate;

  @Column(name = "description")
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @PrePersist
  public void prePersist() {
    if (remainingCapacity == 0) {
      remainingCapacity = totalCapacity;
    }
  }
}
