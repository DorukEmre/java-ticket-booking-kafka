import CartItemEntry from "@/components/CartItemEntry";

import type { CartItem } from "@/types/cart";

function ListUnavailableItems({ items }: { items: CartItem[] }) {
  return (
    <>
      {items.some(item => item.unavailable) && (
        <div>
          <p>Unavailable Items:</p>
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