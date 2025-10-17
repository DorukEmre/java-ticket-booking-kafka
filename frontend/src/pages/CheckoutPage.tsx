import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import { fetchOrderById, makePayment } from "@/api/order";
import PaymentForm from "@/components/PaymentForm";
import ProcessingPayment from "@/components/ProcessingPayment";
import { useCart } from "@/hooks/useCart";

import type { OrderResponse, PaymentRequest } from "@/types/order";
import useDocumentTitle from "@/hooks/useDocumentTitle";


function CheckoutPage() {
  useDocumentTitle("Checkout | Ticket Booking");

  const navigate = useNavigate();
  const location = useLocation();
  const fromCartPage = location.state?.fromCartPage === true;
  const fromOrderDetailsPage = location.state?.fromOrderDetailsPage === true;

  const { cart, deleteCart, totalPrice } = useCart();

  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderResponse | null>(null);

  const [paymentRequest, setPaymentRequest] = useState<PaymentRequest>({ customerName: "", email: "" });
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  useEffect(() => {
    if (!orderId) {
      navigate("/cart");
    }
  }, [orderId]);

  useEffect(() => {
    if (!fromCartPage && !fromOrderDetailsPage) {
      navigate("/cart");
      return;
    }

    async function getOrderDetails() {

      try {
        const response: OrderResponse = await fetchOrderById(orderId!);
        console.log("Order details:", response);
        setOrder(response);

        // If order is INVALID, redirect to cart page
        if (response?.status === "INVALID") {
          console.log("Order INVALID, redirecting to cart");
          navigate(
            `/cart`,
            { state: { fromCheckout: true } }
          );
        }
        // If order is not PENDING_PAYMENT, redirect to order details page
        else if (response?.status !== "PENDING_PAYMENT") {
          console.log("Order not PENDING_PAYMENT, redirecting");
          navigate(
            `/orders/${orderId}`,
            { state: { fromCheckout: true } }
          );
        }

      } catch (error) {
        console.error("Failed to fetch order details:", error);

      }
    }
    getOrderDetails();

  }, []);


  async function handleDeleteCart() {

    try {
      await deleteCart();
      console.log("handleDeleteCart > Cart deleted");

      navigate("/cart");

    } catch (error) {
      console.error("Delete cart failed:", error);

    }
  }

  async function handlePayment(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    console.log("Proceeding to payment with order:", order);

    if (order && order.items.length > 0) {
      try {
        setIsProcessing(true);

        await makePayment(order.orderId, paymentRequest);

        setPaymentRequest({ customerName: "", email: "" });

        console.log("handlePayment > Payment successful, redirecting to confirmation page");

        // redirect handled by <ProcessingPayment />

      } catch (error) {
        console.error("Checkout failed:", error);
      }
    } else {
      console.log("Cart is empty");
    }
  };

  return (
    <div className={`d-flex flex-column align-items-stretch w-100`}>
      {isProcessing ? (

        <ProcessingPayment orderId={order?.orderId} />

      ) : (
        <>
          {order && (
            <div className="w-100 d-flex flex-column flex-grow gap-4 mx-auto" style={{ maxWidth: '480px' }}>

              <div className="d-flex flex-column">
                <h1 className='mb-1'>Checkout</h1>
                {cart &&
                  <button onClick={handleDeleteCart}
                    className="btn p-2 border-1 border-neutral-300 text-neutral-300 control align-self-end">
                    Delete cart
                  </button>}
              </div>

              <div className="d-flex flex-column gap-1 mx-auto">
                <p>Order ID: <span className="fw-bold">{order.orderId}</span></p>
                <p>Status: <span className="bg-danger p-2">{order.status}</span></p>
                <p>Placed at: {new Date(order.placedAt).toLocaleString()}</p>
                <ul>
                  <p>Items:</p>
                  {order.items.map((item) => (
                    <li key={item.id} className="ms-3">
                      <p>
                        <span>{item.ticketCount} {item.ticketCount > 1 ? "tickets" : "ticket"} </span>
                        <span>for event {item.eventId} </span>
                        <span>at {item.ticketPrice.toFixed(2)} each</span>
                      </p>
                    </li>
                  ))}
                </ul>
                <p>Total Price:
                  <span className="fw-bold">{" "}{totalPrice.toFixed(2)}{'\u00A0€'}</span>
                </p>
              </div>

              <PaymentForm
                handlePayment={handlePayment}
                paymentRequest={paymentRequest}
                setPaymentRequest={setPaymentRequest}
                isProcessing={isProcessing}
              />
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default CheckoutPage;