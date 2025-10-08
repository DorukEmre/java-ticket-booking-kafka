import type { CartStatus } from "@/utils/globals";

type CartItem = {
  eventId: number;
  ticketCount: number;
  ticketPrice: number;
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
  totalPrice: number;
};

type CartStatusResponse = {
  cartId: string;
  orderId: string | null;
  status: CartStatusType;
};

type CheckoutRequest = {
  customerName: string;
  email: string;
  items: CartItem[];
};


type CartStatusType = typeof CartStatus[keyof typeof CartStatus];

export { Cart, CartItem, CartResponse, CartIdResponse, CartStatusResponse, CheckoutRequest, CartStatusType };