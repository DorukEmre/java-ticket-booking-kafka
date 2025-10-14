package me.doruk.ticketingcommonlibrary.kafka;

public final class GroupIds {

  private GroupIds() {
    // Prevent instantiation
  }

  // Kafka consumer group ids
  public static final String ORDER_SERVICE = "order-service";
  public static final String CATALOG_SERVICE = "catalog-service";
  public static final String CART_SERVICE = "cart-service";
}