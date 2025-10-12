import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import OrderConfirmationStatus from "@/components/OrderConfirmationStatus";
import ListChangedPriceItems from "@/components/ListChangedPriceItems";
import ListValidItems from "@/components/ListValidItems";
import ListUnavailableItems from "@/components/ListUnavailableItems";

import { useCart } from "@/hooks/useCart";
import { CartStatus } from "@/utils/globals";
import type { CartResponse } from "@/types/cart";
import useDocumentTitle from "@/hooks/useDocumentTitle";

function CartPage() {
  useDocumentTitle("Cart | Ticket Booking");

  const { cart, proceedToCheckout, deleteCart, totalPrice, refreshFromServer } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    console.log("CartPage existing cart:", cart);

    async function redirectToCheckout() {

      if (cart && cart.status === CartStatus.CONFIRMED) {
        console.log("CartPage > Cart status CONFIRMED, redirecting to checkout page");
        try {
          const response: CartResponse = await refreshFromServer();

          if (response.orderId) {
            navigate(`/checkout/${response.orderId}`, { state: { fromCartPage: true } });
          }

        } catch (error) {

        }

      }
    }
    redirectToCheckout();

  }, []);

  async function handleDeleteCart() {

    try {
      await deleteCart();
      console.log("handleDeleteCart > Cart deleted");

    } catch (error) {
      console.error("Delete cart failed:", error);

    }
  }

  async function handleCheckout(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    console.log("Proceeding to checkout with cart:", cart);

    if (cart && cart.items.length > 0) {
      try {
        // let request: CheckoutRequest = {
        //   items: cart ? cart.items.filter(item => !item.unavailable) : []
        // };

        const response = await proceedToCheckout();
        console.log("handleCheckout > proceedToCheckout response:", response);

      } catch (error) {
        console.error("Checkout failed:", error);

      }
    } else {
      console.log("Cart is empty");
    }
  };

  return (
    <>
      {(cart && cart.status != CartStatus.PENDING && cart.status != CartStatus.INVALID) ? (

        <OrderConfirmationStatus handleDeleteCart={handleDeleteCart} />

      ) : (
        <div>
          <div className="pb-3">
            <h1 className='mb-4'>Cart</h1>
            {cart &&
              <button onClick={handleDeleteCart} className="btn p-2 border-1 border-neutral-300 text-neutral-300">Delete cart</button>
            }
          </div>

          <div>
            {cart && cart.items.length > 0 ? (
              <div className="d-flex flex-column gap-2">
                <ListUnavailableItems items={cart.items} />
                <ListChangedPriceItems items={cart.items} />
                <ListValidItems items={cart.items} />
              </div>
            ) : (
              <p>Your cart is empty.</p>
            )}
          </div>

          {cart && cart.items.length > 0 && (
            <div className="d-flex align-items-center gap-4 mt-4">
              <div>
                <p>Total Price: {totalPrice.toFixed(2)}</p>
              </div>

              <div>
                <form onSubmit={handleCheckout}>
                  <button type="submit" className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300">
                    Proceed to checkout
                  </button>
                </form>
              </div>
            </div>
          )}
        </div>
      )}
    </>
  )
}

export default CartPage;
