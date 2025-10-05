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

        .route("images", r -> r
            .path("/images/{filename}")
            .and().method(HttpMethod.GET)
            .uri(uri))

        .route("catalog-events", r -> r
            .path("/events")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        .route("catalog-venues", r -> r
            .path("/venues")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        .route("catalog-event-by-id", r -> r
            .path("/event/{eventId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        .route("catalog-venue-by-id", r -> r
            .path("/venue/{venueId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        // admin routes
        .route("catalog-add-venue", r -> r
            .path("/admin/venue/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.rewritePath("/venue/new", "/api/v1/catalog/add-venue"))
            .uri(uri))

        .route("catalog-add-event", r -> r
            .path("/admin/event/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.rewritePath("/event/new", "/api/v1/catalog/add-event"))
            .uri(uri))

        .build();
  }
}
