CREATE TABLE venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    total_capacity BIGINT NOT NULL
);

CREATE TABLE event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    venue_id BIGINT NOT NULL,
    total_capacity BIGINT NOT NULL,
    remaining_capacity BIGINT NOT NULL,
    ticket_price DECIMAL(10, 2) NOT NULL DEFAULT 10.00,
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id)
      REFERENCES venue(id) 
      ON DELETE CASCADE
);