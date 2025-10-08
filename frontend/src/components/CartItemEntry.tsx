import type { CartItem } from "@/types/cart";

function CartItemEntry({ item, }
  : { item: CartItem; }) {

  return (

    <li>
      Event ID: {item.eventId}, Ticket Count: {item.ticketCount}
    </li>
  )
}

export default CartItemEntry;