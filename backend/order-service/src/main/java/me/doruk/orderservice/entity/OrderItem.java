package me.doruk.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "order_id")
  @NotBlank(message = "Order ID is required")
  private String orderId;

  @Column(name = "event_id")
  @NotNull(message = "Event ID is required")
  private Long eventId;

  @Column(name = "quantity")
  @Min(value = 1, message = "Ticket count must be at least 1")
  private int ticketCount;

  @Column(name = "ticket_price")
  @NotNull(message = "Ticket price is required")
  private BigDecimal ticketPrice;

}
