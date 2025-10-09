package me.doruk.orderservice.model;

public enum OrderStatus {
  VALIDATING, // Checking with catalog-service
  INVALID, // Cart item not valid anymore
  PENDING_PAYMENT, // Validated, waiting for payment
  COMPLETED, // Confirmed
  CANCELLED, // User cancelled
  FAILED // Technical or unrecoverable error
}
