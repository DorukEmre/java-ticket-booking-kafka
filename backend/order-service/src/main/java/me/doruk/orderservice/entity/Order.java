package me.doruk.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`order`")
public class Order {

  @Id
  @Column(name = "id", length = 8)
  private String id;

  @Column(name = "total")
  @Positive(message = "Total price must be positive")
  private BigDecimal totalPrice;

  @CreationTimestamp
  @Column(name = "placed_at", updatable = false, nullable = false)
  @NotNull(message = "Placed date is required")
  private LocalDateTime placedAt;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "status")
  @NotBlank(message = "Status cannot be blank")
  private String status;
}
