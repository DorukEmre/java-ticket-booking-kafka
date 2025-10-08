package me.doruk.orderservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_request_log")
public class OrderRequestLog {
  @Id
  @Column(name = "cart_id", columnDefinition = "BINARY(16)")
  @NotNull(message = "Cart ID is required")
  private UUID cartId;

  @Column(name = "order_id")
  @NotBlank(message = "Order ID is required")
  @Size(max = 32, message = "Order ID must be at most 32 characters")
  private String orderId;

  @CreationTimestamp
  @Column(name = "processed_at", updatable = false, nullable = false)
  @NotNull(message = "Processed date is required")
  private LocalDateTime processedAt;
}
