package me.doruk.gatewayapi.route;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "CATALOG_BASE_URL=localhost:${wiremock.server.port}",
    "CART_BASE_URL=localhost:${wiremock.server.port}",
    "ORDER_BASE_URL=localhost:${wiremock.server.port}",
    "app.cors.allowedOrigins=*"
})
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
class OrderServiceRoutesTest {

  @Autowired
  private WebTestClient webTestClient;

  // public routes

  @Test
  @DisplayName("GET /orders/{orderId} forwards to order-service and returns 200")
  void routesGetOrderById() {
    String orderId = "orderId123";

    stubFor(get(urlEqualTo("/api/v1/orders/" + orderId))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/orders/" + orderId)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /users/id/{customerId}/orders forwards to order-service and returns 200")
  void routesGetOrdersByCustomerId() {
    String customerId = "customerId123";

    stubFor(get(urlEqualTo("/api/v1/users/id/" + customerId + "/orders"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/users/id/" + customerId + "/orders")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("GET /users/email/{email}/orders forwards to order-service and returns 200")
  void routesGetOrdersByEmail() {
    String email = "user@example.com";

    stubFor(get(urlEqualTo("/api/v1/users/email/" + email + "/orders"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/users/email/" + email + "/orders")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("POST /orders/{orderId}/payment forwards to order-service and returns 200")
  void routesOrderPayment() {
    String orderId = "orderId123";

    stubFor(post(urlEqualTo("/api/v1/orders/" + orderId + "/payment"))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.post()
        .uri("/orders/" + orderId + "/payment")
        .bodyValue(new HashMap<>())
        .exchange()
        .expectStatus().isOk();
  }

}