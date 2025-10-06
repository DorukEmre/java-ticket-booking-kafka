
type Venue = {
  venueId: number;
  name: string;
  location: string;
  totalCapacity: number;
};

type Event = {
  eventId: number;
  name: string;
  date: string; // ISO format date string
  venueId: number;
};

export { Venue, Event };