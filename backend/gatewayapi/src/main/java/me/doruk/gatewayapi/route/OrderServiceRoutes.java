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

        .route("orders-by-customerId", r -> r
            .path("/users/id/{customerId}/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("orders-by-email", r -> r
            .path("/users/email/{email}/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("order-payment", r -> r
            .path("/orders/{orderId}/payment")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        // admin routes
        .route("orders", r -> r
            .path("/admin/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/admin/orders", "/api/v1/orders"))
            .uri(uri))

        .route("users", r -> r
            .path("/admin/users")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/admin/users", "/api/v1/users"))
            .uri(uri))

        .route("add-user", r -> r
            .path("/admin/users/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.rewritePath("/admin/users/new", "/api/v1/users/new"))
            .uri(uri))

        .build();
  }

}
