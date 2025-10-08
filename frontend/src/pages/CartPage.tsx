import { useState } from "react";

import CheckoutForm from "@/components/CheckoutForm";
import type { Cart, CartStatusType, CheckoutRequest } from "@/types/cart";
import { CartStatus } from "@/utils/globals";
import { checkoutCart } from "@/api/cart";
import OrderConfirmationStatus from "@/components/OrderConfirmationStatus";
import CartItemEntry from "@/components/CartItemEntry";

function CartPage() {
  const [customerName, setCustomerName] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [cartStatus, setCartStatus] = useState<CartStatusType>(CartStatus.IN_PROGRESS);

  let existingCart = localStorage.getItem("cart");

  let cart: Cart = existingCart
    ? JSON.parse(existingCart)
    : null;

  console.log("CartPage existing cart:", cart);

  if (cart && cart.items.length > 0) {
    console.log("Cart items:", cart.items);
  } else {
    console.log("Cart is empty");
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
        let response = await checkoutCart(cart.cartId, request);
        console.log("Checkout response:", response);

        setCustomerName("");
        setEmail("");
        setCartStatus(CartStatus.PENDING);
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
      {(cartStatus != CartStatus.IN_PROGRESS) ? (
        <OrderConfirmationStatus
          cartid={cart.cartId}
          cartStatus={cartStatus}
          setCartStatus={setCartStatus}
        />
      ) : (
        <>
          <div>
            <p>Cart</p>
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
