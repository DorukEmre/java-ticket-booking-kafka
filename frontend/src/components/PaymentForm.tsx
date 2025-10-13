import { useEffect, useRef, type Dispatch, type FormEventHandler, type SetStateAction } from "react"

import ActionButton from "@/components/ActionButton";
import type { PaymentRequest } from "@/types/order";

function PaymentForm(
  {
    handlePayment,
    paymentRequest,
    setPaymentRequest,
    isProcessing
  }: {
    handlePayment: FormEventHandler<HTMLFormElement>;
    paymentRequest: PaymentRequest;
    setPaymentRequest: Dispatch<SetStateAction<PaymentRequest>>;
    isProcessing: boolean;
  }) {

  const customerNameRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    customerNameRef.current && customerNameRef.current.focus()
  }, [])

  return (
    <form
      onSubmit={handlePayment}
      className="d-flex mt-3 flex-column gap-2"
      style={{ maxWidth: "360px" }}
    >
      <div className="d-flex align-items-center gap-2">
        <label htmlFor="customerName">Name</label>
        <input
          type="text"
          id="customerName"
          ref={customerNameRef}
          onChange={(e) => setPaymentRequest({
            ...paymentRequest,
            customerName: e.target.value
          })}
          value={paymentRequest.customerName}
          required
          autoComplete="name"
        />
      </div>
      <div className="d-flex align-items-center gap-2">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          onChange={(e) => setPaymentRequest({
            ...paymentRequest,
            email: e.target.value
          })}
          value={paymentRequest.email}
          required
          autoComplete="email"
        />
      </div>
      <p>Payment details</p>
      <ActionButton
        text="Pay now"
        clickDisabled={isProcessing}
      />
    </form>)
}

export default PaymentForm;