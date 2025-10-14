import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { fetchOrderById } from "@/api/order";
import type { OrderResponse } from "@/types/order";
import useDocumentTitle from "@/hooks/useDocumentTitle";
import OrderCard from "@/components/OrderCard";


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

        // If order is PENDING_PAYMENT, redirect to checkout page
        if (response?.status === "PENDING_PAYMENT") {
          console.log("Order is PENDING_PAYMENT, redirecting");
          navigate(
            `/checkout/${orderId}`,
            { state: { fromOrderDetailsPage: true } }
          );
        }

      } catch (error: any) {
        console.error("Failed to fetch order details:", error);
        setErrorMsg(error.message ? error.message : "Failed to fetch order details");

      }
    }
    getOrderDetails();

  }, []);

  return (
    <>
      <h1 className="mb-4">Order details</h1>

      {errorMsg && (
        <div className="alert alert-danger" role="alert">
          {errorMsg}
        </div>
      )}

      {order && !errorMsg && (
        <OrderCard order={order} />
      )}
    </>
  )
}

export default OrderDetailsPage