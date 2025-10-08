import { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";

import { fetchOrderById } from "@/api/order";
import type { OrderResponse } from "@/types/order";
import { CartStatus } from "@/utils/globals";
import { useCart } from "@/context/CartContext";


function OrderConfirmationPage() {

  const { deleteCart } = useCart();
  
  const {orderId} = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderResponse | null>(null);

  const navigate = useNavigate();
  const location = useLocation();
  const fromCheckout = location.state?.fromCheckout === true;

  useEffect(() => {
    if (!orderId) {
      navigate("/");
    }
  }, [orderId]);

  useEffect(() => {
    if (!fromCheckout) {
      navigate("/");
      return ;
    }

    async function getOrderDetails() {

      // type OrderItem = {
        // id: number;
        // eventId: number;
        // orderId: string;
        // ticketCount: number;
        // ticketPrice: number;
      // };

      // type OrderResponse = {
      //   orderId: string;
      //   totalPrice: number;
      //   placedAt: string;
      //   customerId: number;
      //   status: string;
      //   items: OrderItem[];
      // }

      try {
        const response: OrderResponse = await fetchOrderById(orderId!);
        console.log("Order details:", response);
        setOrder(response);

        await deleteCart(); // delete cart after successful order
        
      } catch (error) {
        console.error("Failed to fetch order details:", error);
        
      }
    }
    getOrderDetails();

  }, []);
  
  return (
    <>
      <p>Order confirmation</p>
      {order && (
        <div>
          <p>Order ID: <span className="fw-bold">{order.orderId}</span></p>
          <p>Status: <span className={order.status === CartStatus.CONFIRMED ? "bg-success p-2" : "bg-danger p-2"}>{order.status}</span></p>
          <p>Placed At: {new Date(order.placedAt).toLocaleString()}</p>
          {order.status === "CONFIRMED" && (
            <p>Total Price: {order.totalPrice.toFixed(2)}</p>
          )}
          <p>Items:</p>
          <ul>
            {order.items.map((item) => (
              <li key={item.id}>
                <p>
                  <span>{item.ticketCount} {item.ticketCount > 1 ? "tickets" : "ticket"} </span>
                  <span>for event {item.eventId} </span>
                  {order.status === "CONFIRMED" && (
                        <span>at {item.ticketPrice.toFixed(2)} each</span>
                  )}
                </p>
              </li>
            ))}
          </ul>
        </div>
      )}
    </>
  )
}

export default OrderConfirmationPage;