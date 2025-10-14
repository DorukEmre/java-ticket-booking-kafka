import { useState } from "react";

import type { OrderResponse } from "@/types/order";
import { fetchOrderById, fetchOrdersByEmail } from "@/api/order";
import ActionButton from "@/components/ActionButton";
import useDocumentTitle from "@/hooks/useDocumentTitle";
import { checkIcon } from "@/assets";
import OrderCard from "@/components/OrderCard";

function UserOrdersPage() {
  useDocumentTitle("My Orders | Ticket Booking");

  const [orders, setOrders] = useState<OrderResponse[] | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [email, setEmail] = useState<string>("");
  const [orderId, setOrderId] = useState<string>("");
  const [isProcessing, setIsProcessing] = useState(false);


  async function handleGetAllOrders(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!email) {
      setErrorMsg("Please enter an email");
      return;
    }
    console.log("Fetching orders for email:", email);

    setIsProcessing(true);

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

    } finally {
      setIsProcessing(false);
    }
  }

  async function handleGetOrderId(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!orderId) {
      setErrorMsg("Please enter an order ID");
      return;
    }

    console.log("Fetching order for ID:", orderId);

    setIsProcessing(true);

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

    } finally {
      setIsProcessing(false);
    }
  }

  return (
    <>
      <h1 className="mb-4">My Orders</h1>

      <div>
        <form onSubmit={handleGetAllOrders} className="d-flex flex-wrap gap-2 align-items-center">
          <input
            type="email"
            id="email"
            onChange={(e) => setEmail(e.target.value)}
            required
            value={email}
            autoComplete="email"
            placeholder="Enter your email"
          />
          <ActionButton text="Get My Orders"
            icon={checkIcon} animDisabled={!email} clickDisabled={isProcessing} />
        </form>
      </div>

      <div className="my-4">
        <form onSubmit={handleGetOrderId} className="d-flex flex-wrap gap-2 align-items-center">
          <input
            type="text"
            id="orderId"
            onChange={(e) => setOrderId(e.target.value)}
            required
            value={orderId}
            placeholder="Enter your order ID"
          />
          <ActionButton text="Retrieve Order"
            icon={checkIcon} animDisabled={!orderId} clickDisabled={isProcessing} />
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
                <OrderCard order={order} isList key={order.orderId} />
              ))}
            </ul>
          )}

        </div>
      )}
    </>
  )
}

export default UserOrdersPage;