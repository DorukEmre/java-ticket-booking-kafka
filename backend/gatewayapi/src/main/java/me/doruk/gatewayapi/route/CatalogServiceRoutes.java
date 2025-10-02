package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class CatalogServiceRoutes {

  @Value("${CATALOG_BASE_URL}")
  private String baseUrl;

  @Bean
  public RouteLocator catalogRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + baseUrl;

    return builder.routes()
        .route("catalog-events", r -> r
            .path("/catalog/events")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("catalog-venues", r -> r
            .path("/catalog/venues")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("catalog-event-by-id", r -> r
            .path("/catalog/event/{eventId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/catalog/event/(?<eventId>[^/]+)", "/api/v1/catalog/event/${eventId}"))
            .uri(uri))

        .route("catalog-venue-by-id", r -> r
            .path("/catalog/venue/{venueId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.rewritePath("/catalog/venue/(?<venueId>[^/]+)", "/api/v1/catalog/venue/${venueId}"))
            .uri(uri))

        // admin routes
        .route("catalog-add-venue", r -> r
            .path("/catalog/add-venue")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("catalog-add-event", r -> r
            .path("/catalog/add-event")
            .and().method(HttpMethod.POST)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .route("update-event-capacity", r -> r
            .path("/catalog/events/update-capacities")
            .and().method(HttpMethod.PUT)
            .filters(f -> f.prefixPath("/api/v1"))
            .uri(uri))

        .build();
  }
}
