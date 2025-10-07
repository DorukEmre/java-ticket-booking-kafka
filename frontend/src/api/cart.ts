import type { CartItem, CartResponse, CartIdResponse, CartStatusResponse, CheckoutRequest } from '@/types/cart';
import { axiosDeleteWithErrorHandling, axiosGetWithErrorHandling, axiosPostWithErrorHandling, axiosPutWithErrorHandling } from '@/utils/axios';

// GET /cart/:cartId 
async function fetchCartById(cartId: string)
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
async function saveCartItem(cartId: string, item: CartItem)
  : Promise<number> {

  const path = `/cart/${cartId}/items`;

  return axiosPutWithErrorHandling<number>(path, item, true);
}

// DELETE /cart/:cartId/items
async function deleteCartItem(cartId: string, item: CartItem)
  : Promise<number> {

  const path = `/cart/${cartId}/items`;

  return axiosDeleteWithErrorHandling<number>(path, item, true);
}

// POST /cart/:cartId/checkout
async function checkoutCart(cartId: string, request: CheckoutRequest)
  : Promise<number> {

  const path = `/cart/${cartId}/checkout`;

  return axiosPostWithErrorHandling<number>(path, request, true);
}

// GET /cart/:cartId/status
async function fetchCartStatus(cartId: string)
  : Promise<CartStatusResponse> {

  const path = `/cart/${cartId}/status`;

  return axiosGetWithErrorHandling<CartStatusResponse>(path);
}

export { createCart, fetchCartById, saveCartItem, deleteCartItem, checkoutCart, fetchCartStatus };

