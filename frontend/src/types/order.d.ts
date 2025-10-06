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
  orderId: string;
  eventId: number;
  quantity: number;
  pricePerTicket: number;
};

type OrderResponse = {
  orderId: string;
  totalPrice: number;
  placedAt: string;
  customerId: number;
  status: string;
  items: OrderItem[];
}

export { Customer, Order, OrderItem, OrderResponse };