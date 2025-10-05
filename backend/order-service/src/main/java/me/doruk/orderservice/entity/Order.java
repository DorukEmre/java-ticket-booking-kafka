package me.doruk.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;

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
  private BigDecimal totalPrice;

  @CreationTimestamp
  @Column(name = "placed_at", updatable = false, nullable = false)
  private String placedAt;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "status")
  private String status;
}
