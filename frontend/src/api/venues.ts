import axios from 'axios';
import type { Venue } from '@/types/catalog';

export async function fetchVenues(): Promise<Venue[]> {
  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  const url = `${baseURL}/venues`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}