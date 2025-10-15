CREATE TABLE order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id VARCHAR(8) NOT NULL,
  event_id BIGINT NOT NULL,
	quantity BIGINT NOT NULL,
  ticket_price DECIMAL(10,2),
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) 
    REFERENCES "order"(id)
    ON DELETE CASCADE
);