import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { fetchOrderById } from "@/api/order";
import type { OrderResponse } from "@/types/order";
import { CartStatus } from "@/utils/globals";
import useDocumentTitle from "@/hooks/useDocumentTitle";


function OrderDetailsPage() {
  useDocumentTitle("Order Details | Ticket Booking");

  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    if (!orderId) {
      navigate("/");
    }
  }, [orderId]);

  useEffect(() => {

    async function getOrderDetails() {

      try {
        const response: OrderResponse = await fetchOrderById(orderId!);
        console.log("Order details:", response);
        setOrder(response);

      } catch (error: any) {
        console.error("Failed to fetch order details:", error);
        setErrorMsg(error.message ? error.message : "Failed to fetch order details");

      }
    }
    getOrderDetails();

  }, []);

  return (
    <>
      <p>Order details</p>

      {errorMsg && (
        <div className="alert alert-danger" role="alert">
          {errorMsg}
        </div>
      )}

      {order && !errorMsg && (
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

export default OrderDetailsPage