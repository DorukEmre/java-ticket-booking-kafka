import { useState } from "react";

import type { CartStatusType, CheckoutRequest } from "@/types/cart";
import { CartStatus } from "@/utils/globals";
import OrderConfirmationStatus from "@/components/OrderConfirmationStatus";
import CartItemEntry from "@/components/CartItemEntry";
import { useCart } from "@/hooks/useCart";

function CartPage() {
  const { cart, proceedToCheckout, deleteCart, totalPrice } = useCart();

  const [cartStatus, setCartStatus] = useState<CartStatusType>(CartStatus.PENDING);

  console.log("CartPage existing cart:", cart);

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
        let request: CheckoutRequest = {
          items: cart ? cart.items : []
        };

        const response = await proceedToCheckout(request);
        console.log("handleCheckout > proceedToCheckout response:", response);

        setCartStatus(CartStatus.IN_PROGRESS);
        console.log("handleCheckout > proceedToCheckout successful, status set to PENDING");

      } catch (error) {
        console.error("Checkout failed:", error);
        setCartStatus(CartStatus.FAILED);

      }
    } else {
      console.log("Cart is empty");
    }
  };

  return (
    <>
      {(cart && cartStatus != CartStatus.PENDING) ? (
        <OrderConfirmationStatus
          cartid={cart.cartId}
          cartStatus={cartStatus}
          setCartStatus={setCartStatus}
        />
      ) : (
        <div>
          <div className="pb-3">
            <p>Cart</p>
            {cart &&
              <button onClick={handleDeleteCart}>Delete cart</button>
            }
          </div>
          <div>
            {cart && cart.items.length > 0 ? (
              <ul>
                {cart.items.map((item, index) => (
                  <CartItemEntry item={item} key={index} />
                ))}
              </ul>

              // <CartSummary total={...} />

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
