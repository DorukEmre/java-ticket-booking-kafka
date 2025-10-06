package me.doruk.cartservice.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import me.doruk.ticketingcommonlibrary.model.Cart;

@Service
public class CatalogServiceClient {
  // Handles calls going to catalog service

  @Value("${catalog.service.url}")
  private String catalogServiceUrl;

  // Check validity of cart items
  public Map<Long, Boolean> validateCart(final Cart cart) {

    final RestTemplate restTemplate = new RestTemplate();

    String url = catalogServiceUrl + "/validate-cart";

    // To conserve type information of generic types at runtime
    ParameterizedTypeReference<Map<Long, Boolean>> typeRef = new ParameterizedTypeReference<Map<Long, Boolean>>() {
    };

    return restTemplate
        .exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(cart),
            typeRef)
        .getBody();
  }
}
