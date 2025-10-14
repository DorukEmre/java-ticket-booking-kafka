import { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";

import { fetchOrderById } from "@/api/order";
import type { OrderResponse } from "@/types/order";
import { useCart } from "@/hooks/useCart";
import useDocumentTitle from "@/hooks/useDocumentTitle";
import OrderCard from "@/components/OrderCard";


function OrderConfirmationPage() {
  useDocumentTitle("Confirmation | Ticket Booking");

  const { deleteCart } = useCart();

  const { orderId } = useParams<{ orderId: string }>();
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
      return;
    }

    async function getOrderDetails() {

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
      <h1 className='mb-4'>Order confirmation</h1>

      {order && order.status === "COMPLETED" && (
        <p>Your order has been confirmed! Thank you for your purchase.</p>
      )}
      {order && order.status === "FAILED" && (
        <p>There was an issue with your order. Please try again or contact support.</p>
      )}

      {order && (
        <OrderCard order={order} />
      )}
    </>
  )
}

export default OrderConfirmationPage;