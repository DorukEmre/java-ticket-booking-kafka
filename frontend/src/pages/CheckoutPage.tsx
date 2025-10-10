import { useState } from "react";

function CheckoutPage() {

  const [customerName, setCustomerName] = useState<string>("");
  const [email, setEmail] = useState<string>("");


  async function handleCheckout(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    // console.log("Proceeding to checkout with cart:", cart);

    // if (cart && cart.items.length > 0) {
    //   try {
    //     let request: CheckoutRequest = {
    //       customerName,
    //       email,
    //       items: cart ? cart.items : []
    //     };

    //     await checkout(request);

    //     setCustomerName("");
    //     setEmail("");
    //     setCartStatus(CartStatus.PENDING);
    //     console.log("handleCheckout > Checkout successful, status set to PENDING");

    //   } catch (error) {
    //     console.error("Checkout failed:", error);
    //     setCartStatus(CartStatus.FAILED);

    //   }
    // } else {
    //   console.log("Cart is empty");
    // }
  };

  return (
    <div>
      <h1>Checkout Page</h1>
      <p>This is where users will review their cart and enter payment details.</p>
    </div>
  );
}

export default CheckoutPage;