ALTER TABLE event
ADD COLUMN image_url VARCHAR(255) DEFAULT 'default-event.jpg';

ALTER TABLE venue
ADD COLUMN image_url VARCHAR(255) DEFAULT 'default-venue.jpg';