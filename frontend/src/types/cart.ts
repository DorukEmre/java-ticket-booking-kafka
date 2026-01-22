import type { CartStatus } from "@/utils/globals";

type CartItem = {
  eventId: number;
  ticketCount: number;
  ticketPrice: number;

  previousPrice?: number | null;
  priceChanged?: boolean;
  unavailable?: boolean;
};

type Cart = {
  cartId: string;
  items: CartItem[];
  status: CartStatusType;
};

type CartIdResponse = {
  cartId: string;
};

type CartResponse = {
  cartId: string;
  orderId: string | null;
  status: CartStatusType;
  items: CartItem[];
};

type CheckoutRequest = {
  items: CartItem[];
};

type CartContextType = {
  cart: Cart | null;
  setCartLocal: (c: Cart) => void;
  addOrUpdateItem: (item: CartItem) => Promise<void>;
  deleteCartAndUpdateItem: (item: CartItem) => Promise<void>;
  removeItem: (item: CartItem) => Promise<void>;
  deleteCart: () => Promise<void>;
  renewCart: () => Promise<void>;
  refreshFromServer: () => Promise<CartResponse>;
  proceedToCheckout: () => Promise<void>;
  totalPrice: number;
};

type CartStatusType = typeof CartStatus[keyof typeof CartStatus];

export type {
  Cart, CartItem, CartResponse, CartIdResponse,
  CheckoutRequest, CartStatusType, CartContextType
};