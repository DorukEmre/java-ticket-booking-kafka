import axios from 'axios';
import type { CartItem, CartResponse, CartIdResponse, CartStatusResponse, CheckoutRequest } from '@/types/cart';
import { getBaseUrl } from '@/utils/utils';
import type { ApiErrorResponse } from '@/types/error';

// GET /cart/:cartId 
async function fetchCartById(cartId: number)
  : Promise<CartResponse | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart/${cartId}`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data;
}

// POST /cart
async function createCart()
  : Promise<CartIdResponse | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart`;

  const response = await axios.post(url, {}, { withCredentials: true });

  return response.data;
}

// PUT /cart/:cartId/items
async function saveCartItem(cartId: number, item: CartItem)
  : Promise<void | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart/${cartId}/items`;

  const response = await axios.put(url, item, { withCredentials: true });

  return response.data;
}

// DELETE /cart/:cartId/items
async function deleteCartItem(cartId: number, item: CartItem)
  : Promise<void | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart/${cartId}/items`;

  const response = await axios.delete(url, { data: item, withCredentials: true });

  return response.data;
}

// POST /cart/:cartId/checkout
async function checkoutCart(cartId: number, request: CheckoutRequest)
  : Promise<void | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart/${cartId}/checkout`;

  const response = await axios.post(url, request, { withCredentials: true });

  return response.data;
}

// GET /cart/:cartId/status
async function fetchCartStatus(cartId: number)
  : Promise<CartStatusResponse | ApiErrorResponse> {

  const baseURL = getBaseUrl();
  const url = `${baseURL}/cart/${cartId}/status`;

  const response = await axios.get(url, { withCredentials: true });

  return response.data.status;
}

export { createCart, fetchCartById, saveCartItem, deleteCartItem, checkoutCart, fetchCartStatus };

