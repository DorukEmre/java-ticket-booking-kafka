import CartItemEntry from "@/components/CartItemEntry";

import type { CartItem } from "@/types/cart";

function ListChangedPriceItems({ items }: { items: CartItem[] }) {
  return (
    <>
      {items.some(item => item.priceChanged) && (
        <div>
          <p>Items with Changed Prices:</p>
          <ul>
            {items
              .filter(item => item.priceChanged)
              .map((item, index) => (
                <CartItemEntry item={item} key={index} />
              ))}
          </ul>
        </div>
      )}
    </>
  )
}

export default ListChangedPriceItems;