CREATE TABLE order_request_log (
  cart_id BINARY(16) PRIMARY KEY,
  order_id VARCHAR(8),
  processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_request_log_order_id
    FOREIGN KEY (order_id) REFERENCES `order`(id)
);
