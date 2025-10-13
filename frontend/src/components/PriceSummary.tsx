function PriceSummary({ totalPrice }: { totalPrice: number }) {

  return (

    <div>
      <p>Total Price: {totalPrice.toFixed(2)}{'\u00A0€'}</p>
    </div>

  );
}

export default PriceSummary;