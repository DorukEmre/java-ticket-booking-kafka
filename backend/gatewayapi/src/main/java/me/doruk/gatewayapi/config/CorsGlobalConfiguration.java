package me.doruk.gatewayapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsGlobalConfiguration {

  @Value("${app.cors.allowedOrigins}")
  String origins;

  @Bean
  public CorsWebFilter corsWebFilter() {

    String[] allowedOrigins = origins.split(",");

    CorsConfiguration corsConfig = new CorsConfiguration();
    // Specify frontend URL instead of "*" for better security
    // corsConfig.addAllowedOriginPattern("*");
    for (String origin : allowedOrigins) {
      corsConfig.addAllowedOrigin(origin);
    }
    corsConfig.setAllowCredentials(true);

    corsConfig.addAllowedMethod("*");
    corsConfig.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}