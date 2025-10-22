ALTER TABLE event
  ADD COLUMN event_date DATE;

UPDATE event 
  SET event_date = CURRENT_DATE WHERE event_date IS NULL;

ALTER TABLE event
  MODIFY COLUMN event_date DATE NOT NULL;

ALTER TABLE event
  ADD COLUMN description TEXT;