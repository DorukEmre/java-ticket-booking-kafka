import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import debitCard from "@/assets/icon_debit_card.png";

function ProcessingPayment({ orderId }: { orderId: string | undefined }) {

  const navigate = useNavigate();

  useEffect(() => {

    // Simulate processing delay
    const timer = setTimeout(() => {

      navigate(`/orders/${orderId}/confirmation`, { state: { fromCheckout: true } });

    }, 2000);

    return () => clearTimeout(timer);
  }, []);
  return (
    <div>
      <img src={debitCard} alt="Processing Payment" style={{ width: '150px', marginBottom: '20px' }} />
      <p>Processing payment, please wait...</p>
    </div>
  )
}

export default ProcessingPayment;