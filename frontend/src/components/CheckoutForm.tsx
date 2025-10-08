import { useEffect, useRef, type Dispatch, type FormEventHandler, type SetStateAction } from "react"

function CheckoutForm(
  {
    handleCheckout,
    customerName,
    email,
    setCustomerName,
    setEmail
  }: {
    handleCheckout: FormEventHandler<HTMLFormElement>;
    customerName: string;
    email: string;
    setCustomerName: Dispatch<SetStateAction<string>>;
    setEmail: Dispatch<SetStateAction<string>>;
  }) {
  const customerNameRef = useRef<HTMLInputElement>(null);


  useEffect(() => {
    customerNameRef.current && customerNameRef.current.focus()
  }, [])

  return (
    <form onSubmit={handleCheckout}>
      <div className="">
        <label htmlFor="customerName">Name</label>
        <input
          type="text"
          id="customerName"
          ref={customerNameRef}
          onChange={(e) => setCustomerName(e.target.value)}
          value={customerName}
          required
          autoComplete="name"
        />
      </div>
      <div className="">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          onChange={(e) => setEmail(e.target.value)}
          required
          value={email}
          autoComplete="email"
        />
      </div>
      <button className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300">Checkout</button>
    </form>)
}

export default CheckoutForm;