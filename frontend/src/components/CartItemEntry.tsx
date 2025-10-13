import { useQuery } from "@tanstack/react-query";

import { useCart } from "@/hooks/useCart";
import { imageBaseUrl } from "@/utils/globals";
import { fetchEventById } from "@/api/catalog";
import queryClient from "@/config/queryClient";

import type { CartItem } from "@/types/cart";
import type { Event } from "@/types/catalog";

import { deleteIcon } from "@/assets"
import TicketQuantitySelector from "./TicketQuantitySelector";
import { useState } from "react";
import { Link } from "react-router-dom";


function CartItemEntry({ item }
  : { item: CartItem; }) {

  const { removeItem } = useCart();
  const [ticketCount, setTicketCount] = useState<number>(item.ticketCount);

  // Fetch event details for the cart item
  const eventQuery = useQuery<Event>({
    queryKey: ["event", item.eventId],
    queryFn: () => fetchEventById(item.eventId),
    initialData: () => {
      const events = queryClient.getQueryData<Event[]>(["events"]);
      return events?.find((ev) => ev.id === item.eventId);
    },
  });

  const { data: event } = eventQuery;


  async function handleDeleteItem() {
    try {
      await removeItem(item);

    } catch (error) {
      console.error("Delete item failed:", error);

    }
  }

  return (
    <>
      {/* Unavailable */}
      {item.unavailable && (
        <li className="d-flex align-items-center gap-3 mb-3">
          <p className="m-0">
            Event ID: {item.eventId}, Ticket Count: {item.ticketCount}
          </p>
        </li>
      )}

      {/* Price changed */}
      {item.priceChanged && !item.unavailable && (
        <li className="d-flex align-items-center gap-3 mb-3">
          <p className="">
            Event ID: {item.eventId}, Ticket Count: {item.ticketCount}, Price: {item.ticketPrice}
          </p>
          <button type="button" onClick={handleDeleteItem}
            className={"btn p-1 border-1 border-neutral-300 control"}
            aria-label={`Remove tickets for event ${item.eventId}`}
            title="Remove from cart"
          >
            <img src={deleteIcon} width="18" height="18" aria-hidden="true" />
          </button>
        </li>
      )}

      {/* Valid */}
      {!item.priceChanged && !item.unavailable && (
        <li className="row align-items-center g-3 mb-5" style={{ maxWidth: '800px' }}>

          <div className="col-sm-3">
            {event?.imageUrl && (
              <img
                className="rounded-2"
                src={imageBaseUrl + event.imageUrl}
                alt={event.name}
              />
            )}
          </div>

          <div className="col-sm-7 d-flex flex-column justify-content-between">

            <div className="d-flex justify-content-between align-items-center mb-2">
              <Link to={`/events/${item.eventId}`} className="fs-5">
                {event?.name} (id: {item.eventId})
              </Link>
              <p className="">
                {item.ticketPrice}{'\u00A0€'}
              </p>
            </div>

            <div className="d-flex justify-content-start align-items-center gap-4">
              <TicketQuantitySelector ticketCount={ticketCount} setTicketCount={setTicketCount} />
              <button type="button" onClick={handleDeleteItem}
                className={"btn p-1 border-1 border-neutral-300 control"}
                aria-label={`Remove tickets for event ${item.eventId}`}
                title="Remove from cart"
              >
                <img src={deleteIcon} width="18" height="18" aria-hidden="true" />
              </button>
            </div>

          </div>

        </li>
      )}
    </>
  );
}

export default CartItemEntry;