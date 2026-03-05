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
class CatalogServiceRoutesTest {

  @Autowired
  private WebTestClient webTestClient;

  // routes to static images

  @Test
  @DisplayName("GET /images/{filename} returns 200")
  void routesImages() {
    String filename = "default-event.jpg";

    stubFor(get(urlEqualTo("/images/" + filename))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/images/" + filename)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /images/events/{filename} returns 200")
  void routesImagesEvents() {
    String filename = "default-event.jpg";

    stubFor(get(urlEqualTo("/images/events/" + filename))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/images/events/" + filename)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /images/venues/{filename} returns 200")
  void routesImagesVenues() {
    String filename = "default-venue.jpg";

    stubFor(get(urlEqualTo("/images/venues/" + filename))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/images/venues/" + filename)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /images/nonexistent.png returns 404")
  void routesImagesNotFound() {
    String filename = "nonexistent.png";

    stubFor(get(urlEqualTo("/images/" + filename))
        .willReturn(aResponse().withStatus(404)));

    webTestClient.get()
        .uri("/images/" + filename)
        .exchange()
        .expectStatus().isNotFound();
  }

  // public routes

  @Test
  @DisplayName("GET /events returns 200")
  void routesEvents() {
    stubFor(get(urlEqualTo("/api/v1/catalog/events"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/events")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /venues returns 200")
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
  @DisplayName("GET /venues/456 returns 200")
  void routesVenueById() {
    stubFor(get(urlEqualTo("/api/v1/catalog/venues/456"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/venues/456")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /nonexistent/path returns 404")
  void routesNotFound() {
    stubFor(get(urlEqualTo("/api/v1/catalog/nonexistent/path"))
        .willReturn(aResponse().withStatus(404)));

    webTestClient.get()
        .uri("/nonexistent/path")
        .exchange()
        .expectStatus().isNotFound();
  }

}