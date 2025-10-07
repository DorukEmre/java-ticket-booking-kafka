import { fetchCartStatus } from "@/api/cart";
import type { CartStatusResponse, CartStatusType } from "@/types/cart";
import { CartStatus } from "@/utils/utils";
import { useEffect, useState, type Dispatch, type SetStateAction } from "react";

function OrderConfirmationStatus(
  { cartStatus,
    cartid,
    setCartStatus
  }: {
    cartStatus: CartStatusType;
    cartid: string;
    setCartStatus: Dispatch<SetStateAction<CartStatusType>>;
  }) {
  const [orderId, setOrderId] = useState<string | null>(null);

  useEffect(() => {
    if (cartStatus !== CartStatus.PENDING)
      return;

    const interval = setInterval(async () => {
      try {
        const cartStatusResponse: CartStatusResponse = await fetchCartStatus(cartid);
        console.log("Fetched cart status:", cartStatusResponse);

        if (cartStatusResponse.status !== cartStatus) {
          setCartStatus(cartStatusResponse.status);
        }
        if (!cartStatusResponse.orderId) {
          setOrderId(cartStatusResponse.orderId);
        }
      } catch (error) {
        console.error("Error fetching cart status:", error);
      }
    }, 1000);

    return () => clearInterval(interval);

  }, [cartid, cartStatus]);

  return (
    <div>
      {cartStatus === CartStatus.PENDING && (
        <>
          <p>Your order {orderId} is <span className="bg-warning p-2">{cartStatus}</span></p>
          <p>Your order is being processed. Please wait for confirmation.</p>
        </>
      )}

      {cartStatus === CartStatus.CONFIRMED && (
        <>
          <p>Your order {orderId} is <span className="bg-success p-2">{cartStatus}</span></p>
          <p>Your order has been confirmed! Thank you for your purchase.</p>
        </>
      )}

      {cartStatus === CartStatus.FAILED && (
        <>
          <p>Your order {orderId} is <span className="bg-danger p-2">{cartStatus}</span></p>
          <p>There was an issue with your order. Please try again or contact support.</p>
        </>
      )}

    </div>
  );
}

export default OrderConfirmationStatus;