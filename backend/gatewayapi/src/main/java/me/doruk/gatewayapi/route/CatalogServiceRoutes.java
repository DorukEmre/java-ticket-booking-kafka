package me.doruk.gatewayapi.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import reactor.core.publisher.Mono;

@Configuration
public class CatalogServiceRoutes {

  @Value("${CATALOG_BASE_URL}")
  private String baseUrl;

  @Bean
  public RouteLocator catalogRoutes(RouteLocatorBuilder builder) {

    String uri = "http://" + baseUrl;

    return builder.routes()

        // routes to static images

        .route("images", r -> r
            .path("/images/{filename}")
            .and().method(HttpMethod.GET)
            .uri(uri))

        .route("images-events", r -> r
            .path("/images/events/{filename}")
            .and().method(HttpMethod.GET)
            .uri(uri))

        .route("images-venues", r -> r
            .path("/images/venues/{filename}")
            .and().method(HttpMethod.GET)
            .uri(uri))

        // public routes

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
            .path("/events/{eventId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        .route("catalog-venue-by-id", r -> r
            .path("/venues/{venueId}")
            .and().method(HttpMethod.GET)
            .filters(f -> f.prefixPath("/api/v1/catalog"))
            .uri(uri))

        // admin routes

        .route("catalog-add-venue", r -> r
            .path("/admin/venues/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.rewritePath("/admin/venues/new", "/api/v1/catalog/add-venue"))
            .uri(uri))

        .route("catalog-add-event", r -> r
            .path("/admin/events/new")
            .and().method(HttpMethod.POST)
            .filters(f -> f.rewritePath("/admin/events/new", "/api/v1/catalog/add-event"))
            .uri(uri))

        // documentation route

        .route("catalog-service-api-docs", r -> r
            .path("/docs/catalog")
            .and().method(HttpMethod.GET)
            .filters(f -> f
                .rewritePath("/docs/catalog", "/v3/api-docs")
                .modifyResponseBody(String.class, String.class, (exchange, body) -> {
                  if (body == null)
                    return Mono.empty();
                  String modified = body.replace("/api/v1/catalog", "");
                  return Mono.just(modified);
                }))
            .uri(uri))

        .build();
  }

}
