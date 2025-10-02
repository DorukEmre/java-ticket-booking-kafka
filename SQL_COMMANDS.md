### Add user/customer
```sql
SELECT * FROM booking_db.customer;

INSERT INTO booking_db.customer(name, email)
VALUES ("Bob", "bob@email.com");
```

### Add event
```sql
SELECT * FROM catalog_db.event;

INSERT INTO catalog_db.event (name, venue_id, total_capacity, remaining_capacity)
VALUES ('Sample Event', 1, 100, 100);
```

### SQL Commands for failed migration
For instance:
```sql
DELETE FROM booking_db.flyway_schema_history
WHERE version = 1;

SET SQL_SAFE_UPDATES = 0;
```