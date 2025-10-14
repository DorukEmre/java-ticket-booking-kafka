package me.doruk.ticketingcommonlibrary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledRequested {
  private UUID cartId;
  private String orderId;
}
