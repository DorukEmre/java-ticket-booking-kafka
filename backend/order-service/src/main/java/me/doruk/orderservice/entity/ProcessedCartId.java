package me.doruk.orderservice.entity;

import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "processed_cart_id")
public class ProcessedCartId {
  @Id
  @Column(name = "cart_id", columnDefinition = "BINARY(16)")
  private UUID cartId;

  @CreationTimestamp
  @Column(name = "processed_at", updatable = false, nullable = false)
  private String processedAt;
}
