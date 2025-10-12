import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import { fetchOrderById, makePayment } from "@/api/order";
import PaymentForm from "@/components/PaymentForm";
import ProcessingPayment from "@/components/ProcessingPayment";
import { useCart } from "@/hooks/useCart";

import type { OrderResponse, PaymentRequest } from "@/types/order";
import { CartStatus } from "@/utils/globals";
import useDocumentTitle from "@/hooks/useDocumentTitle";


function CheckoutPage() {
  useDocumentTitle("Checkout | Ticket Booking");

  const navigate = useNavigate();
  const location = useLocation();
  const fromCartPage = location.state?.fromCartPage === true;

  const { cart, deleteCart } = useCart();

  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderResponse | null>(null);

  const [paymentRequest, setPaymentRequest] = useState<PaymentRequest>({ customerName: "", email: "" });
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  useEffect(() => {
    if (!orderId) {
      navigate("/");
    }
  }, [orderId]);

  useEffect(() => {
    if (!fromCartPage) {
      navigate("/");
      return;
    }

    async function getOrderDetails() {

      try {
        const response: OrderResponse = await fetchOrderById(orderId!);
        console.log("Order details:", response);
        setOrder(response);

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

        // redirect inside <ProcessingPayment />

      } catch (error) {
        console.error("Checkout failed:", error);

      }
    } else {
      console.log("Cart is empty");
    }
  };

  return (
    <>
      <h1>Checkout Page</h1>
      {isProcessing
        ? <ProcessingPayment orderId={order?.orderId} />
        : (
          <>
            {order && (
              <div>
                {cart &&
                  <button onClick={handleDeleteCart}>Delete cart</button>
                }

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

                <PaymentForm
                  handlePayment={handlePayment}
                  paymentRequest={paymentRequest}
                  setPaymentRequest={setPaymentRequest}
                />
              </div>
            )}
          </>
        )}
    </>
  )
}

export default CheckoutPage;