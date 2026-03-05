package me.doruk.gatewayapi.route;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
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
class CartServiceRoutesTest {

  @Autowired
  private WebTestClient webTestClient;

  // public routes

  @Test
  @DisplayName("POST /cart returns 201")
  void routesCreateCart() {
    stubFor(post(urlEqualTo("/api/v1/cart"))
        .willReturn(aResponse().withStatus(201)));

    webTestClient.post()
        .uri("/cart")
        .exchange()
        .expectStatus().isCreated();
  }

  @Test
  @DisplayName("GET /cart/{cartId} returns 200")
  void routesGetCart() {
    String cartId = "12345";

    stubFor(get(urlEqualTo("/api/v1/cart/" + cartId))
        .willReturn(aResponse().withStatus(200)));

    webTestClient.get()
        .uri("/cart/" + cartId)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("PUT /cart/{cartId}/items returns 201")
  void routesSaveItem() {
    String cartId = "12345";

    stubFor(put(urlEqualTo("/api/v1/cart/" + cartId + "/items"))
        .willReturn(aResponse().withStatus(201)));

    webTestClient.put()
        .uri("/cart/" + cartId + "/items")
        .bodyValue(new HashMap<>())
        .exchange()
        .expectStatus().isCreated();
  }

  @Test
  @DisplayName("DELETE /cart/{cartId}/items returns 204")
  void routesDeleteItem() {
    String cartId = "12345";

    stubFor(delete(urlEqualTo("/api/v1/cart/" + cartId + "/items"))
        .willReturn(aResponse().withStatus(204)));

    webTestClient.delete()
        .uri("/cart/" + cartId + "/items")
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("DELETE /cart/{cartId} returns 204")
  void routesDeleteCart() {
    String cartId = "12345";

    stubFor(delete(urlEqualTo("/api/v1/cart/" + cartId))
        .willReturn(aResponse().withStatus(204)));

    webTestClient.delete()
        .uri("/cart/" + cartId)
        .exchange()
        .expectStatus().isNoContent();
  }

}