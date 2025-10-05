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
@Table(name = "order_request_log")
public class OrderRequestLog {
  @Id
  @Column(name = "cart_id", columnDefinition = "BINARY(16)")
  private UUID cartId;

  @Column(name = "order_id")
  private String orderId;

  @CreationTimestamp
  @Column(name = "processed_at", updatable = false, nullable = false)
  private String processedAt;
}
