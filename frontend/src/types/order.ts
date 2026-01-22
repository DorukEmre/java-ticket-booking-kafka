type Customer = {
  customerName: string;
  email: string;
};

type Order = {
  orderId: string;
  totalPrice: number;
  placedAt: string;
  customerId: number;
  status: string;
};

type OrderItem = {
  id: number;
  eventId: number;
  orderId: string;
  ticketCount: number;
  ticketPrice: number;
};

type OrderResponse = {
  orderId: string;
  totalPrice: number;
  placedAt: string;
  customerId: number;
  status: string;
  items: OrderItem[];
}

type PaymentRequest = {
  customerName: string;
  email: string;
}

export type { Customer, Order, OrderItem, OrderResponse, PaymentRequest };