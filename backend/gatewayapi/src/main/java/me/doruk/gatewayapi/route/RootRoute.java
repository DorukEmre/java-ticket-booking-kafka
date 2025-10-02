package me.doruk.gatewayapi.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RootRoute {

  @Bean
  public RouterFunction<ServerResponse> rootRouteFunc() {
    return route(GET("/"), req -> ServerResponse.ok().bodyValue(
        java.util.Map.of(
            "status", "ok",
            "message", "live")));
  }
}
