package me.doruk.orderService.entity;

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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "total")
  private BigDecimal totalPrice;

  @Column(name = "quantity")
  private Long ticketCount;

  @CreationTimestamp
  @Column(name = "placed_at", updatable = false, nullable = false)
  private String placedAt;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "event_id")
  private Long eventId;
}
