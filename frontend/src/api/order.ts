import axios from 'axios';
import type { OrderResponse } from '@/types/order';
import type { ApiErrorResponse } from '@/types/error';
import { getBaseUrl } from '@/utils/utils';

// GET /orders/:orderId
async function fetchOrderById(orderId: string)
  : Promise<OrderResponse | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/orders/${orderId}`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

// GET /events/:customerId
async function fetchOrdersByCustomerId(customerId: number)
  : Promise<OrderResponse[] | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/events/${customerId}`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

export { fetchOrderById, fetchOrdersByCustomerId };