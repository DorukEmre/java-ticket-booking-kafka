
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useCart } from "@/hooks/useCart";
import type { CartResponse } from "@/types/cart";
import { CartStatus } from "@/utils/globals";

function OrderPollingStatus(
  { handleDeleteCart }: { handleDeleteCart: () => Promise<void> }
) {
  const [orderId, setOrderId] = useState<string | null>(null);
  const navigate = useNavigate();
  const { cart, refreshFromServer } = useCart();

  // Poll for cart status while pending
  useEffect(() => {
    if (cart?.status !== CartStatus.IN_PROGRESS)
      return;

    const interval = setInterval(async () => {
      try {
        const response: CartResponse = await refreshFromServer();
        console.log("Polling cart status:", response);

        if (response.orderId) {
          setOrderId(response.orderId);
        }

      } catch (error) {
        console.error("Error fetching cart status:", error);
      }
    }, 500);

    return () => clearInterval(interval);

  }, [cart?.status]);

  useEffect(() => {
    if (orderId && (cart?.status === CartStatus.CONFIRMED || cart?.status === CartStatus.FAILED)) {
      navigate(`/checkout/${orderId}`, { state: { fromCartPage: true } });
    }
  }, [orderId]);

  return (
    <div>
      {cart?.status === CartStatus.IN_PROGRESS && (
        <>
          <p>Your order {orderId} is <span className="p-2">{cart?.status}</span></p>
          <p>Your order is being processed. Please wait for confirmation.</p>
        </>
      )}

      {!orderId && cart?.status === CartStatus.CONFIRMED && (
        <>
          <p>Your order {orderId} is <span className="bg-success p-2">{cart?.status}</span></p>
          <p>Your order has been confirmed! Thank you for your purchase.</p>
        </>
      )}

      {!orderId && cart?.status === CartStatus.FAILED && (
        <>
          <p>Your order {orderId} is <span className="bg-danger p-2">{cart?.status}</span></p>
          <p>There was an issue with your order. Please try again or contact support.</p>
          {cart &&
            <button onClick={handleDeleteCart}>Delete cart</button>
          }
        </>
      )}

    </div>
  );
}

export default OrderPollingStatus;