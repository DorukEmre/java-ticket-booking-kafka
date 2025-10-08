package me.doruk.catalogservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "venue")
public class Venue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  @NotBlank(message = "Name must not be blank")
  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  @Column(name = "location")
  @NotBlank(message = "Location must not be blank")
  @Size(max = 255, message = "Location must not exceed 255 characters")
  private String location;

  @Column(name = "total_capacity")
  @Min(value = 1, message = "Total capacity must be at least 1")
  private int totalCapacity;

  @Column(name = "image_url")
  @Size(max = 512, message = "Image URL must not exceed 512 characters")
  private String imageUrl;
}
