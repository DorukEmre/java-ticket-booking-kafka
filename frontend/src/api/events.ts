import axios from 'axios';
import type { Event } from '@/types/catalog';

export async function fetchEvents(): Promise<Event[]> {
  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  const url = `${baseURL}/events`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}