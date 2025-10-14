import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import { debitCard } from "@/assets";

function ProcessingPayment({ orderId }: { orderId: string | undefined }) {

  const navigate = useNavigate();

  useEffect(() => {

    // Simulate processing delay
    const timer = setTimeout(() => {

      navigate(`/orders/${orderId}/confirmation`, { state: { fromCheckout: true } });

    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="w-100 d-flex flex-column flex-grow gap-4 mx-auto" style={{ maxWidth: '480px' }}>
      <img
        src={debitCard}
        alt="Processing Payment"
        className="mx-auto"
        style={{ width: '150px' }} />
      <p className="text-center">Processing payment, please wait...</p>
    </div>
  )
}

export default ProcessingPayment;