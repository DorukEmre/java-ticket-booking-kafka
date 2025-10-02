package me.doruk.cartservice.client;

import me.doruk.cartservice.response.CatalogServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatalogServiceClient {
  // Handles calls going to catalog service

  @Value("${catalog.service.url}")
  private String catalogServiceUrl;

  public CatalogServiceResponse getCatalogService(final Long eventId) {
    final RestTemplate restTemplate = new RestTemplate();

    String url = catalogServiceUrl + "/event/" + eventId;

    return restTemplate.getForObject(url, CatalogServiceResponse.class);
  }
}
