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
import ActionButton from "@/components/ActionButton";
import PriceSummary from "@/components/PriceSummary";

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
    <div className={`d-flex flex-column ${(cart?.items?.length ?? 0) > 0 ? "align-items-center" : ""}`}>
      {(cart && cart.status != CartStatus.PENDING && cart.status != CartStatus.INVALID) ? (

        <OrderConfirmationStatus handleDeleteCart={handleDeleteCart} />

      ) : (
        <div style={{ maxWidth: '640px' }}>
          <div className="pb-3 d-flex flex-column">
            <h1 className='mb-4'>Cart</h1>
            {cart &&
              <button onClick={handleDeleteCart}
                className="btn p-2 border-1 border-neutral-300 text-neutral-300 control align-self-end">
                Delete cart
              </button>}
          </div>

          {cart && cart.items.length > 0 ? (
            <div className="d-flex flex-column gap-2">
              <ListUnavailableItems items={cart.items} />
              <ListChangedPriceItems items={cart.items} />
              <ListValidItems items={cart.items} />
            </div>
          ) : (
            <p>Your cart is empty.</p>
          )}

          {cart && cart.items.length > 0 && (
            <div className="d-flex flex-column align-items-end gap-4 mt-4">

              <PriceSummary totalPrice={totalPrice} />

              <form onSubmit={handleCheckout}>
                <ActionButton text="Proceed to checkout" />
              </form>

            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default CartPage;
