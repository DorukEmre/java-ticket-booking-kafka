import { useState } from "react";

import CheckoutForm from "@/components/CheckoutForm";
import type { CartStatusType, CheckoutRequest } from "@/types/cart";
import { CartStatus } from "@/utils/globals";
import OrderConfirmationStatus from "@/components/OrderConfirmationStatus";
import CartItemEntry from "@/components/CartItemEntry";
import { useCart } from "@/context/CartContext";

function CartPage() {
  const { cart, checkout, deleteCart } = useCart();

  const [customerName, setCustomerName] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [cartStatus, setCartStatus] = useState<CartStatusType>(CartStatus.IN_PROGRESS);

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
          customerName,
          email,
          items: cart ? cart.items : []
        };

        await checkout(request);

        setCustomerName("");
        setEmail("");
        setCartStatus(CartStatus.PENDING);
        console.log("handleCheckout > Checkout successful, status set to PENDING");

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
      {(cart && cartStatus != CartStatus.IN_PROGRESS) ? (
        <OrderConfirmationStatus
          cartid={cart.cartId}
          cartStatus={cartStatus}
          setCartStatus={setCartStatus}
        />
      ) : (
        <>
          <div>
            <p>Cart</p>
            {cart &&
              <button onClick={handleDeleteCart}>Delete cart</button>
            }
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

          <div>
            {cart && cart.items.length > 0 && (
              <CheckoutForm
                handleCheckout={handleCheckout}
                customerName={customerName}
                email={email}
                setCustomerName={setCustomerName}
                setEmail={setEmail}
              />
            )}
          </div>
        </>
      )}
    </>
  )
}

export default CartPage;
