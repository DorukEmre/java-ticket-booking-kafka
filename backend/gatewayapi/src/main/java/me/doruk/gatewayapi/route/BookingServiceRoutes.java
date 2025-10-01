package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class BookingServiceRoutes {

  @Value("${BOOKING_BASE_URL}")
  private String bookingBaseUrl;

  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + bookingBaseUrl;

    return builder.routes()
        .route("booking-users", r -> r
            .path("/booking/users")
            .and()
            .method(HttpMethod.GET)
            // .filters(f -> f.rewritePath("/booking/users", "/api/v1/booking/users"))
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("booking-add-user", r -> r
            .path("/booking/add-user")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("booking", r -> r
            .path("/booking")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }

}
