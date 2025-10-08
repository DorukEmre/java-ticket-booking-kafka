import type { OrderResponse } from '@/types/order';
import { axiosGetWithErrorHandling } from '@/utils/axios';

// GET /orders/:orderId
async function fetchOrderById(orderId: string)
  : Promise<OrderResponse> {

  const path = `/orders/${orderId}`;

  return axiosGetWithErrorHandling<OrderResponse>(path);
}

// GET /users/id/:customerId/orders
async function fetchOrdersByCustomerId(customerId: number)
  : Promise<OrderResponse[]> {

  const path = `/users/id/${customerId}/orders`;

  return axiosGetWithErrorHandling<OrderResponse[]>(path);
}

// GET /users/email/:email/orders
async function fetchOrdersByEmail(email: string)
  : Promise<OrderResponse[]> {

  const path = `/users/email/${email}/orders`;

  return axiosGetWithErrorHandling<OrderResponse[]>(path);
}

export { fetchOrderById, fetchOrdersByCustomerId, fetchOrdersByEmail };