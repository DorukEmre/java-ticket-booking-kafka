import type { CartItem, CartResponse, CartIdResponse, CheckoutRequest } from '@/types/cart';
import { axiosDeleteWithErrorHandling, axiosGetWithErrorHandling, axiosPostWithErrorHandling, axiosPutWithErrorHandling } from '@/utils/axios';

// GET /cart/:cartId 
async function apiFetchCartById(cartId: string)
  : Promise<CartResponse> {

  const path = `/cart/${cartId}`;

  return axiosGetWithErrorHandling<CartResponse>(path);
}

// POST /cart
async function apiCreateCart()
  : Promise<CartIdResponse> {

  const path = `/cart`;

  return axiosPostWithErrorHandling<CartIdResponse>(path, {});
}

// PUT /cart/:cartId/items
async function apiSaveCartItem(cartId: string, item: CartItem)
  : Promise<number> {

  const path = `/cart/${cartId}/items`;

  return axiosPutWithErrorHandling<number>(path, item, true);
}

// DELETE /cart/:cartId/items
async function apiDeleteCartItem(cartId: string, item: CartItem)
  : Promise<number> {

  const path = `/cart/${cartId}/items`;

  return axiosDeleteWithErrorHandling<number>(path, item, true);
}

// DELETE /cart/:cartId
async function apiDeleteCart(cartId: string)
  : Promise<number> {

  const path = `/cart/${cartId}`;

  return axiosDeleteWithErrorHandling<number>(path, {}, true);
}

// POST /cart/:cartId/checkout
async function apiCheckoutCart(cartId: string, request: CheckoutRequest)
  : Promise<number> {

  const path = `/cart/${cartId}/checkout`;

  return axiosPostWithErrorHandling<number>(path, request, true);
}


export {
  apiCreateCart, apiFetchCartById, apiSaveCartItem, apiDeleteCartItem, apiDeleteCart, apiCheckoutCart,
};

