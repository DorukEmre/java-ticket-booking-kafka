ALTER TABLE event ADD COLUMN image_url VARCHAR(255);
UPDATE event SET image_url = 'default-event.jpg' WHERE image_url IS NULL;
ALTER TABLE event ALTER COLUMN image_url SET DEFAULT 'default-event.jpg';

ALTER TABLE venue ADD COLUMN image_url VARCHAR(255);
UPDATE venue SET image_url = 'default-venue.jpg' WHERE image_url IS NULL;
ALTER TABLE venue ALTER COLUMN image_url SET DEFAULT 'default-venue.jpg';