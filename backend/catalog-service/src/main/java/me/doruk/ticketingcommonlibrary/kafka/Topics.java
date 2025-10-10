package me.doruk.ticketingcommonlibrary.kafka;

public final class Topics {

  private Topics() {
    // Prevent instantiation
  }

  // cart-service to order-service
  public static final String ORDER_REQUESTED = "order-requested";
  public static final String ORDER_CANCELLED = "order-cancelled";

  // order-service to cart-service
  public static final String ORDER_FAILED = "order-failed";
  public static final String ORDER_INVALID = "order-invalid";
  public static final String ORDER_SUCCEEDED = "order-succeeded";

  // order-service to catalog-service
  public static final String RESERVE_INVENTORY = "reserve-inventory";
  public static final String RELEASE_INVENTORY = "release-inventory";

  // catalog-service to order-service
  public static final String INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";
  public static final String INVENTORY_RESERVATION_INVALID = "inventory-reservation-invalid";
  public static final String INVENTORY_RESERVATION_SUCCEEDED = "inventory-reservation-succeeded";
}
