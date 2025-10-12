import { useCart } from "@/hooks/useCart";

import type { CartItem } from "@/types/cart";

import { deleteIcon } from "@/assets"

function CartItemEntry({ item }
  : { item: CartItem; }) {

  const { removeItem } = useCart();

  async function handleDeleteItem() {
    try {
      await removeItem(item);

    } catch (error) {
      console.error("Delete item failed:", error);

    }
  }

  return (
    <>
      {item.unavailable && (
        <li className="d-flex align-items-center gap-3 mb-3">
          <p className="m-0">
            Event ID: {item.eventId}, Ticket Count: {item.ticketCount}
          </p>
        </li>
      )}

      {item.priceChanged && !item.unavailable && (
        <li className="d-flex align-items-center gap-3 mb-3">
          <p className="m-0">
            Event ID: {item.eventId}, Ticket Count: {item.ticketCount}, Price: {item.ticketPrice}
          </p>
          <button type="button" onClick={handleDeleteItem}
            className={"btn p-1 border-1 border-neutral-300"}
            aria-label={`Remove tickets for event ${item.eventId}`}
            title="Remove from cart"
          >
            <img src={deleteIcon} width="18" height="18" aria-hidden="true" />
          </button>
        </li>
      )}

      {!item.priceChanged && !item.unavailable && (
        <li className="d-flex align-items-center gap-3 mb-3">
          <p className="m-0">
            Event ID: {item.eventId}, Ticket Count: {item.ticketCount}, Price: {item.ticketPrice}
          </p>
          <button type="button" onClick={handleDeleteItem}
            className={"btn p-1 border-1 border-neutral-300"}
            aria-label={`Remove tickets for event ${item.eventId}`}
            title="Remove from cart"
          >
            <img src={deleteIcon} width="18" height="18" aria-hidden="true" />
          </button>
        </li>
      )}
    </>
  );
}

export default CartItemEntry;