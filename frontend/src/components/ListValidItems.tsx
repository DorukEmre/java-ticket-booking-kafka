import CartItemEntry from "@/components/CartItemEntry";

import type { CartItem } from "@/types/cart";

function ListValidItems({ items }: { items: CartItem[] }) {
  return (
    <>
      {items.some(item => !item.priceChanged && !item.unavailable) && (
        <div>
          <p>Valid Items:</p>
          <ul>
            {items
              .filter(item => !item.priceChanged && !item.unavailable)
              .map((item, index) => (
                <CartItemEntry item={item} key={index} />
              ))}
          </ul>
        </div>
      )}
    </>
  )

}

export default ListValidItems;