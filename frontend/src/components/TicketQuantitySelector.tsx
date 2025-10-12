import type { Dispatch, SetStateAction } from "react";

import { addIcon, removeIcon } from "@/assets";

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
      <div className="d-flex align-items-center justify-content-center
       border border-1 border-neutral-300 rounded-2 overflow-hidden">
        <button type="button"
          style={{ width: "28px", height: "28px" }}
          onClick={decrement}
          aria-label="Decrease tickets"
          className="p-0 border-0 bg-transparent d-flex align-items-center justify-content-center"
        >
          <img src={removeIcon} aria-hidden="true" />
        </button>
        <p
          className="d-flex align-items-center justify-content-center border-start border-end border-1 text-neutral-300"
          style={{ width: "28px", height: "28px" }}
        >
          {ticketCount}
        </p>
        <button type="button"
          style={{ width: "28px", height: "28px" }}
          onClick={increment}
          aria-label="Increase tickets"
          className="p-0 border-0 bg-transparent d-flex align-items-center justify-content-center"
        >
          <img src={addIcon} aria-hidden="true" />
        </button>
      </div>
    </>
  )
}

export default TicketQuantitySelector;
