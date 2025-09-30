package me.doruk.orderService.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

  @Value("${inventory.service.url}")
  private String inventoryServiceUrl;

  public ResponseEntity<Void> updateInventory(final Long eventId, final Long ticketCount) {
    final RestTemplate restTemplate = new RestTemplate();

    String url = inventoryServiceUrl + "/event/" + eventId + "/capacity/" + ticketCount;
    restTemplate.put(url, null);

    return ResponseEntity.ok().build();
  }
}
