package me.doruk.gatewayapi.route;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "CATALOG_BASE_URL=localhost:${wiremock.server.port}",
    "CART_BASE_URL=http://localhost:${wiremock.server.port}",
    "ORDER_BASE_URL=http://localhost:${wiremock.server.port}",
    "app.cors.allowedOrigins=*"
})
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
class CatalogRoutesTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void routesEvents() {
    stubFor(get(urlEqualTo("/api/v1/catalog/events"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/events")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void routesVenues() {
    stubFor(get(urlEqualTo("/api/v1/catalog/venues"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/venues")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /events/123 returns 200")
  void routesEventById() {
    stubFor(get(urlEqualTo("/api/v1/catalog/events/123"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/events/123")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void routesVenueById() {
    stubFor(get(urlEqualTo("/api/v1/catalog/venues/456"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/venues/456")
        .exchange()
        .expectStatus().isOk();
  }

}