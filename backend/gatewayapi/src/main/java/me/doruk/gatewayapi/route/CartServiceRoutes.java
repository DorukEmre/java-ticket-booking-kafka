package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class CartServiceRoutes {

  @Value("${CART_BASE_URL}")
  private String baseUrl;

  @Bean
  public RouteLocator cartRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + baseUrl;

    return builder.routes()

        .route("create-cart", r -> r
            .path("/cart")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("get-cart", r -> r
            .path("/cart/{cartId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("save-item", r -> r
            .path("/cart/{cartId}/items")
            .and().method(HttpMethod.PUT)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("delete-item", r -> r
            .path("/cart/{cartId}/items")
            .and().method(HttpMethod.DELETE)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("delete-cart", r -> r
            .path("/cart/{cartId}")
            .and().method(HttpMethod.DELETE)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("checkout", r -> r
            .path("/cart/{cartId}/checkout")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("cart-status", r -> r
            .path("/cart/{cartId}/status")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }

}
