ALTER TABLE `order`
ADD CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE;