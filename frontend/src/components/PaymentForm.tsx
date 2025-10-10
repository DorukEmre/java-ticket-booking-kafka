import { useEffect, useRef, type Dispatch, type FormEventHandler, type SetStateAction } from "react"

function PaymentForm(
  {
    handlePayment,
    customerName,
    email,
    setCustomerName,
    setEmail
  }: {
    handlePayment: FormEventHandler<HTMLFormElement>;
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
    <form onSubmit={handlePayment}>
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
      <button className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300">Pay now</button>
    </form>)
}

export default PaymentForm;