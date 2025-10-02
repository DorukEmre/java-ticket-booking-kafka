package me.doruk.orderService.client;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

  @Value("${inventory.service.url}")
  private String inventoryServiceUrl;

  // Send the list as JSON
  public ResponseEntity<Void> updateInventory(List<SimpleEntry<Long, Long>> eventTicketCounts) {
    final RestTemplate restTemplate = new RestTemplate();

    String url = inventoryServiceUrl + "/events/update-capacities";
    restTemplate.put(url, eventTicketCounts);

    return ResponseEntity.ok().build();
  }
}
