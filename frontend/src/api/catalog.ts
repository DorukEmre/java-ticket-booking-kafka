import axios from 'axios';
import type { Event, Venue } from '@/types/catalog';
import { getBaseUrl } from '@/utils/utils';
import type { ApiErrorResponse } from '@/types/error';

// GET /venues
async function fetchVenues()
  : Promise<Venue[]> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/venues`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

// GET /venues/:venueId
async function fetchVenueById(venueId: number)
  : Promise<Venue | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/venues/${venueId}`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

// GET /events
async function fetchEvents()
  : Promise<Event[]> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/events`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

// GET /events/:eventId
async function fetchEventById(eventId: number)
  : Promise<Event | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/events/${eventId}`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

export { fetchVenues, fetchVenueById, fetchEvents, fetchEventById };