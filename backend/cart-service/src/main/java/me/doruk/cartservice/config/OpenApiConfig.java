package me.doruk.cartservice.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  @Value("${springdoc.server.url:}")
  private String serverUrl;

  @Bean
  public OpenAPI cartServiceAPI() {
    OpenAPI openAPI = new OpenAPI()
        .info(new Info()
            .title("Cart Service API")
            .description("Cart Service API documentation")
            .version("1.0.0"));

    if (!serverUrl.isBlank()) {
      openAPI.servers(List.of(new Server()
          .url(serverUrl)
          .description("Public API Server")));
    }

    return openAPI;

  }

}
