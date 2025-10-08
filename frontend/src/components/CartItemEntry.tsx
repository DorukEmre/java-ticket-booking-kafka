import { useCart } from "@/context/CartContext";
import type { CartItem } from "@/types/cart";

function CartItemEntry({ item, }
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
    <li className="d-flex align-items-center gap-3">
      <p>Event ID: {item.eventId}, Ticket Count: {item.ticketCount}, Price: {item.ticketPrice}</p>
      <button type="button" onClick={handleDeleteItem}>Remove</button>
    </li>
  )
}

export default CartItemEntry;