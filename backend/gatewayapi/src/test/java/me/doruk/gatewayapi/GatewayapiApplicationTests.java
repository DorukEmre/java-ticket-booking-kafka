package me.doruk.gatewayapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "CATALOG_BASE_URL=http://localhost:8081",
    "CART_BASE_URL=http://localhost:8082",
    "ORDER_BASE_URL=http://localhost:8083",
    "app.cors.allowedOrigins=*"
})
class GatewayapiApplicationTests {

  @Test
  void contextLoads() {
  }

}
