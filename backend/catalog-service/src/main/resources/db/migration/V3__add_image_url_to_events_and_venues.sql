ALTER TABLE event
  ADD COLUMN image_url VARCHAR(255) DEFAULT 'default-event.jpg';

UPDATE event 
  SET image_url = 'default-event.jpg' WHERE image_url IS NULL;

ALTER TABLE venue 
  ADD COLUMN image_url VARCHAR(255) DEFAULT 'default-venue.jpg';

UPDATE venue 
  SET image_url = 'default-venue.jpg' WHERE image_url IS NULL;
