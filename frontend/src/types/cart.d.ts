type CartItem = {
  eventId: number;
  ticketCount: number;
  ticketPrice?: number;
};

type Cart = {
  cartId: string;
  items: CartItem[];
};

type CartIdResponse = {
  cartId: string;
};

type CartStatus = {
  PENDING: 'PENDING';
  IN_PROGRESS: 'IN_PROGRESS';
  CONFIRMED: 'CONFIRMED';
  FAILED: 'FAILED';
}

type CartResponse = {
  cartId: string;
  orderId: string | null;
  status: CartStatus[keyof CartStatus];
  items: CartItem[];
  totalPrice: number;
  status: string;
};

type CartStatusResponse = {
  cartId: string;
  orderId: string | null;
  status: CartStatus[keyof CartStatus];
};

type CheckoutRequest = {
  customerName: string;
  email: string;
  items: CartItem[];
};

export { Cart, CartItem, CartResponse, CartIdResponse, CartStatusResponse, CartStatus, CheckoutRequest };