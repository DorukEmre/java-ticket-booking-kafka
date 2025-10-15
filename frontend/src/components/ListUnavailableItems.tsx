import CartItemEntry from "@/components/CartItemEntry";

import type { CartItem } from "@/types/cart";

function ListUnavailableItems({ items }: { items: CartItem[] }) {
  return (
    <>
      {items.some(item => item.unavailable) && (
        <div className="alert alert-danger pb-2">
          <p>Items unavailable:</p>
          <ul>
            {items
              .filter(item => item.unavailable)
              .map((item, index) => (
                <CartItemEntry item={item} key={index} />
              ))}
          </ul>
        </div>
      )}
    </>
  );
}

export default ListUnavailableItems;