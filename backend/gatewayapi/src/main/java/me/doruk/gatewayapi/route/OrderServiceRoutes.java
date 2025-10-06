package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class OrderServiceRoutes {

  @Value("${ORDER_BASE_URL}")
  private String baseUrl;

  @Bean
  public RouteLocator orderRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + baseUrl;

    return builder.routes()

        .route("order-by-id", r -> r
            .path("/orders/{orderId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("orders-for-customerId", r -> r
            .path("/users/{customerId}/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        // admin routes
        .route("orders", r -> r
            .path("/admin/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("users", r -> r
            .path("/admin/users")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("add-user", r -> r
            .path("/admin/users/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }

}
