import type { Dispatch, SetStateAction } from "react";

function TicketQuantitySelector({
  ticketCount,
  setTicketCount,
}: {
  ticketCount: number;
  setTicketCount: Dispatch<SetStateAction<number>>;
}) {

  function decrement() {
    if (ticketCount > 1) {
      setTicketCount(ticketCount => ticketCount - 1);
    }
  }

  function increment() {
    setTicketCount(ticketCount => ticketCount + 1);
  }

  return (
    <>
      <div className="d-flex align-items-center gap-2">
        <button style={{ width: "28px", height: "28px" }} onClick={decrement}>
          -
        </button>
        <p className="mb-0">{ticketCount}</p>
        <button style={{ width: "28px", height: "28px" }} onClick={increment}>
          +
        </button>
      </div>
    </>
  )
}

export default TicketQuantitySelector;
