import type { CartItem } from "@/types/cart";

function CartItemEntry(
  {
    item,
    index
  }: {
    item: CartItem;
    index: number
  }) {
  return (

    <li key={index}>
      Event ID: {item.eventId}, Ticket Count: {item.ticketCount}
    </li>
  )
}

export default CartItemEntry;