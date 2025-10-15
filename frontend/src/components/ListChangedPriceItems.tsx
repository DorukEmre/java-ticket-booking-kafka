import CartItemEntry from "@/components/CartItemEntry";

import type { CartItem } from "@/types/cart";

function ListChangedPriceItems({ items }: { items: CartItem[] }) {
  return (
    <>
      {items.some(item => item.priceChanged) && (
        <div className="border-start border-2 border-compl-300 px-3 pt-1 pb-0">
          <p className="mb-2">Price of item changed:</p>
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