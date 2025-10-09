package me.doruk.cartservice.model;

public enum CartStatus {
  PENDING, // Items added, idle
  IN_PROGRESS, // Checkout started
  INVALID, // Cart item not valid anymore
  CONFIRMED, // Final price accepted
  FAILED, // Non-recoverable error
  EXPIRED // TTL expired / abandoned
}