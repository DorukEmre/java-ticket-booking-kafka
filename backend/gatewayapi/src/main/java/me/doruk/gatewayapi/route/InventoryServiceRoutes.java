package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class InventoryServiceRoutes {

  @Value("${INVENTORY_BASE_URL}")
  private String baseUrl;

  @Bean
  public RouteLocator inventoryRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + baseUrl;

    return builder.routes()
        .route("inventory-events", r -> r
            .path("/inventory/events")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("inventory-venues", r -> r
            .path("/inventory/venues")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("inventory-event-by-id", r -> r
            .path("/inventory/event/{eventId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/inventory/event/(?<eventId>[^/]+)", "/api/v1/inventory/event/${eventId}"))
            .uri(uri))

        .route("inventory-venue-by-id", r -> r
            .path("/inventory/venue/{venueId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/inventory/venue/(?<venueId>[^/]+)", "/api/v1/inventory/venue/${venueId}"))
            .uri(uri))

        // admin routes
        .route("inventory-add-venue", r -> r
            .path("/inventory/add-venue")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("inventory-add-event", r -> r
            .path("/inventory/add-event")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("update-event-capacity", r -> r
            .path("/inventory/events/update-capacities")
            .and().method(HttpMethod.PUT)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }
}
