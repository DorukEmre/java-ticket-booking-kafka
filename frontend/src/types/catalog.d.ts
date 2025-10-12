type Venue = {
  id: number;
  name: string;
  location: string;
  totalCapacity: number;
  imageUrl: string;
}

type Event = {
  id: number;
  name: string;
  capacity: number;
  venue: Venue;
  ticketPrice: number;
  eventDate: string;
  description: string;
  imageUrl: string;
}

export { Venue, Event };
