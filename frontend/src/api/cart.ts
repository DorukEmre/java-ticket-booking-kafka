import type { CartItem, CartResponse, CartIdResponse, CartStatusResponse, CheckoutRequest } from '@/types/cart';
import { axiosDeleteWithErrorHandling, axiosGetWithErrorHandling, axiosPostWithErrorHandling, axiosPutWithErrorHandling } from '@/utils/axios';

// GET /cart/:cartId 
async function fetchCartById(cartId: number)
  : Promise<CartResponse> {

  const path = `/cart/${cartId}`;

  return axiosGetWithErrorHandling<CartResponse>(path);
}

// POST /cart
async function createCart()
  : Promise<CartIdResponse> {

  const path = `/cart`;

  return axiosPostWithErrorHandling<CartIdResponse>(path, {});
}

// PUT /cart/:cartId/items
async function saveCartItem(cartId: number, item: CartItem)
  : Promise<void> {

  const path = `/cart/${cartId}/items`;

  return axiosPutWithErrorHandling<void>(path, item);
}

// DELETE /cart/:cartId/items
async function deleteCartItem(cartId: number, item: CartItem)
  : Promise<void> {

  const path = `/cart/${cartId}/items`;

  return axiosDeleteWithErrorHandling<void>(path, item);
}

// POST /cart/:cartId/checkout
async function checkoutCart(cartId: number, request: CheckoutRequest)
  : Promise<void> {

  const path = `/cart/${cartId}/checkout`;

  return axiosPostWithErrorHandling<void>(path, request);
}

// GET /cart/:cartId/status
async function fetchCartStatus(cartId: number)
  : Promise<CartStatusResponse> {

  const path = `/cart/${cartId}/status`;

  return axiosGetWithErrorHandling<CartStatusResponse>(path);
}

export { createCart, fetchCartById, saveCartItem, deleteCartItem, checkoutCart, fetchCartStatus };

