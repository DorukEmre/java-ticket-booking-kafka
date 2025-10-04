ALTER TABLE `order`
MODIFY total DECIMAL(10,2),
ADD CHECK (
  status != 'COMPLETED' OR total IS NOT NULL
);
