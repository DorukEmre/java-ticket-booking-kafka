package me.doruk.orderService.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import me.doruk.orderService.dto.TicketCountForEvent;

@Service
public class CatalogServiceClient {

  @Value("${catalog.service.url}")
  private String catalogServiceUrl;

  // Send the list as JSON
  public ResponseEntity<Void> updateCatalogService(List<TicketCountForEvent> eventTicketCounts) {
    final RestTemplate restTemplate = new RestTemplate();

    String url = catalogServiceUrl + "/events/update-capacities";
    restTemplate.put(url, eventTicketCounts);

    return ResponseEntity.ok().build();
  }
}
