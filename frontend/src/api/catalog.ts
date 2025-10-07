import type { Event, Venue } from '@/types/catalog';
import { axiosGetWithErrorHandling } from '@/utils/axios';


// GET /venues
async function fetchVenues()
  : Promise<Venue[]> {

  const path = `/venues`;

  return axiosGetWithErrorHandling<Venue[]>(path);
}

// GET /venues/:venueId
async function fetchVenueById(venueId: number)
  : Promise<Venue> {

  const path = `/venues/${venueId}`;

  return axiosGetWithErrorHandling<Venue>(path);
}

// GET /events
async function fetchEvents()
  : Promise<Event[]> {

  const path = `/events`;

  return axiosGetWithErrorHandling<Event[]>(path);
}

// GET /events/:eventId
async function fetchEventById(eventId: number)
  : Promise<Event> {

  const path = `/events/${eventId}`;

  return axiosGetWithErrorHandling<Event>(path);
}

export { fetchVenues, fetchVenueById, fetchEvents, fetchEventById };