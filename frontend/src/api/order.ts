import type { OrderResponse } from '@/types/order';
import { axiosGetWithErrorHandling } from '@/utils/axios';

// GET /orders/:orderId
async function fetchOrderById(orderId: string)
  : Promise<OrderResponse> {

  const path = `/orders/${orderId}`;

  return axiosGetWithErrorHandling<OrderResponse>(path);
}

// GET /events/:customerId
async function fetchOrdersByCustomerId(customerId: number)
  : Promise<OrderResponse[]> {

  const path = `/events/${customerId}`;

  return axiosGetWithErrorHandling<OrderResponse[]>(path);
}

export { fetchOrderById, fetchOrdersByCustomerId };