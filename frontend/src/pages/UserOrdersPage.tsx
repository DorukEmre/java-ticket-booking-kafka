import { useState } from "react";
import { Link } from "react-router-dom";

import type { OrderResponse } from "@/types/order";
import { CartStatus } from "@/utils/globals";
import { fetchOrderById, fetchOrdersByEmail } from "@/api/order";
import ActionButton from "@/components/ActionButton";
import useDocumentTitle from "@/hooks/useDocumentTitle";

function UserOrdersPage() {
  useDocumentTitle("My Orders | Ticket Booking");

  const [orders, setOrders] = useState<OrderResponse[] | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [email, setEmail] = useState<string>("");
  const [orderId, setOrderId] = useState<string>("");

  async function handleGetAllOrders(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!email) {
      setErrorMsg("Please enter an email");
      return;
    }

    console.log("Fetching orders for email:", email);

    try {
      const response = await fetchOrdersByEmail(email!);
      console.log("Orders fetched:", response);
      setOrders(response);
      setEmail("");
      setOrderId("");
      setErrorMsg(null);

    } catch (error: unknown) {
      console.error("Failed to fetch order:", error);

      if (typeof error === "object" && error !== null && "message" in error && typeof (error as any).message === "string") {
        setErrorMsg((error as { message: string }).message);

      } else {
        setErrorMsg(String(error) || "Failed to fetch order");
      }
      setOrders(null);
      setOrderId("");

    }
  }

  async function handleGetOrderId(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!orderId) {
      setErrorMsg("Please enter an order ID");
      return;
    }

    console.log("Fetching order for ID:", orderId);

    try {
      const response = await fetchOrderById(orderId!);
      console.log("Order fetched:", response);
      setOrders([response]);
      setEmail("");
      setOrderId("");
      setErrorMsg(null);

    } catch (error: unknown) {
      console.error("Failed to fetch order:", error);

      if (typeof error === "object" && error !== null && "message" in error && typeof (error as any).message === "string") {
        setErrorMsg((error as { message: string }).message);

      } else {
        setErrorMsg(String(error) || "Failed to fetch order");
      }
      setOrders(null);
      setEmail("");

    }
  }

  return (
    <>
      <h1 className="mb-4">My Orders</h1>

      <div>
        <form onSubmit={handleGetAllOrders} className="d-flex gap-2 align-items-center">
          <input
            type="email"
            id="email"
            onChange={(e) => setEmail(e.target.value)}
            required
            value={email}
            autoComplete="email"
            placeholder="Enter your email"
          />
          <ActionButton text="Get My Orders" />
        </form>
      </div>

      <div className="my-3">
        <form onSubmit={handleGetOrderId} className="d-flex gap-2 align-items-center">
          <input
            type="text"
            id="orderId"
            onChange={(e) => setOrderId(e.target.value)}
            required
            value={orderId}
            placeholder="Enter your order ID"
          />
          <ActionButton text="Retrieve Order" />
        </form>
      </div>

      {errorMsg && (
        <div className="alert alert-danger" role="alert">
          {errorMsg}
        </div>
      )}

      {orders && !errorMsg && (
        <div>
          {orders.length === 0 ? (
            <p>No orders found for this email.</p>
          ) : (
            <ul>
              {orders.map((order) => (
                <li key={order.orderId} className="mb-3 p-3 border rounded">
                  <p>Order ID:{""}
                    <Link to={`/orders/${order.orderId}`} className="text-decoration-none">
                      <span className="fw-bold">
                        {order.orderId}
                      </span>
                    </Link>
                  </p>
                  <p>Status: <span className={order.status === CartStatus.CONFIRMED ? "bg-success p-2" : "bg-danger p-2"}>{order.status}</span></p>
                  {order.status === "CONFIRMED" && (
                    <p>Total Price: {order.totalPrice.toFixed(2)}</p>
                  )}
                  <p>Placed At: {new Date(order.placedAt).toLocaleString()}</p>
                  <div>
                    <h5>Items:</h5>
                    <ul>
                      {order.items.map((item) => (
                        <li key={item.id}>
                          Event ID: {item.eventId}, Ticket Count: {item.ticketCount}, Ticket Price: {item.ticketPrice?.toFixed(2)}
                        </li>
                      ))}
                    </ul>
                  </div>
                </li>
              ))}
            </ul>
          )}

        </div>
      )}
    </>
  )
}

export default UserOrdersPage;