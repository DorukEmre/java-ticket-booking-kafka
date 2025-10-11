import { useState } from "react";
import { Link } from "react-router-dom";

import type { OrderResponse } from "@/types/order";
import { CartStatus } from "@/utils/globals";
import { fetchOrdersByEmail } from "@/api/order";
import ActionButton from "@/components/ActionButton";

function UserOrdersPage() {

  const [orders, setOrders] = useState<OrderResponse[] | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [email, setEmail] = useState<string>("");

  async function handleGetOrders(e: React.FormEvent<HTMLFormElement>) {
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
      setErrorMsg(null);

    } catch (error: unknown) {
      console.error("Failed to fetch orders:", error);
      if (error instanceof Error) {
        setErrorMsg(error.message);
      } else {
        setErrorMsg(String(error) || "Failed to fetch orders");
      }
      setOrders(null);

    }
  }


  // type OrderItem = {
  //   id: number;
  //   eventId: number;
  //   orderId: string;
  //   ticketCount: number;
  //   ticketPrice: number;
  // };

  // type OrderResponse = {
  //   orderId: string;
  //   totalPrice: number;
  //   placedAt: string;
  //   customerId: number;
  //   status: string;
  //   items: OrderItem[];
  // }

  return (
    <>
      <div>User Orders Page</div>
      <div>
        <form onSubmit={handleGetOrders} className="d-flex gap-2 align-items-center">
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
      {errorMsg && (
        <div className="alert alert-danger" role="alert">
          {errorMsg}
        </div>
      )}
      {orders && !errorMsg && (
        <div>
          <h3 className="mt-4">My Orders</h3>
          {orders.length === 0 ? (
            <p>No orders found for this email.</p>
          ) : (
            <ul>
              {orders.map((order) => (
                <li key={order.orderId} className="mb-3 p-3 border rounded">
                  <p>Order ID:
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