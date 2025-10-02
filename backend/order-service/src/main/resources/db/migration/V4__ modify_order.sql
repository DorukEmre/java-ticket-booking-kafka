ALTER TABLE `order`
DROP COLUMN quantity,
DROP COLUMN event_id,
ADD CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE;