import type { Event, Venue } from '@/types/catalog';
import { getBaseUrl } from '@/utils/utils';
import { axiosGetWithErrorHandling } from '@/utils/axios';


// GET /venues
async function fetchVenues()
  : Promise<Venue[]> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/venues`;

  return axiosGetWithErrorHandling<Venue[]>(url);
}

// GET /venues/:venueId
async function fetchVenueById(venueId: number)
  : Promise<Venue> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/venues/${venueId}`;

  return axiosGetWithErrorHandling<Venue>(url);
}

// GET /events
async function fetchEvents()
  : Promise<Event[]> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/events`;

  return axiosGetWithErrorHandling<Event[]>(url);
}

// GET /events/:eventId
async function fetchEventById(eventId: number)
  : Promise<Event> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/events/${eventId}`;

  return axiosGetWithErrorHandling<Event>(url);
}

export { fetchVenues, fetchVenueById, fetchEvents, fetchEventById };