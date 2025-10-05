RENAME TABLE processed_cart_id TO order_request_log;

ALTER TABLE order_request_log
  ADD COLUMN order_id BIGINT AFTER cart_id,
  ADD CONSTRAINT uq_order_request_log_cart_id UNIQUE (cart_id),
  ADD CONSTRAINT fk_order_request_log_order_id
    FOREIGN KEY (order_id) REFERENCES `order`(id);
