package me.doruk.orderservice.config;

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
  public OpenAPI orderServiceAPI() {
    OpenAPI openAPI = new OpenAPI()
        .info(new Info()
            .title("Order Service API")
            .description("Order Service API documentation")
            .version("1.0.0"));

    if (!serverUrl.isBlank()) {
      openAPI.servers(List.of(new Server()
          .url(serverUrl)
          .description("Public API Server")));
    }

    return openAPI;

  }

}
