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
        .route("orders", r -> r
            .path("/orders")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }

}
