type Venue = {
  venueId: number;
  name: string;
  location: string;
  totalCapacity: number;
  imageUrl: string;
}

type Event = {
  eventId: number;
  name: string;
  capacity: number;
  venue: Venue;
  ticketPrice: number;
  eventDate: string; // ISO format date string
  description: string;
  imageUrl: string;
}

export { Venue, Event, VenueResponse, EventResponse };