import type { Cart, CartItem, CheckoutRequest } from "@/types/cart";
import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import {
  apiCreateCart,
  apiSaveCartItem,
  apiDeleteCartItem,
  apiFetchCartById,
  apiCheckoutCart,
} from "@/api/cart";

type CartContextType = {
  cart: Cart | null;
  setCartLocal: (c: Cart) => void;
  addOrUpdateItem: (item: CartItem) => Promise<void>;
  // removeItem: (item: CartItem) => Promise<void>;
  // refreshFromServer: () => Promise<void>;
  checkout: (request: CheckoutRequest) => Promise<void>;
};

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartProvider({ children }: { children: ReactNode }) {

  const [cart, setCart] = useState<Cart | null>(null);

  useEffect(() => {
    // Load cart from localStorage if there is one. If parsing fails, clear it.
    try {
      const localCart = localStorage.getItem("cart");
      if (localCart)
        setCart(JSON.parse(localCart) as Cart);
    } catch (error) {
      localStorage.removeItem("cart");
    }
  }, []);


  // Save to localStorage
  const setCartLocal = (c: Cart) => {
    setCart(c);
    localStorage.setItem("cart", JSON.stringify(c));
  };

  // Create cart and save it if none present
  async function ensureCartId(): Promise<string | null> {

    if (!cart || cart.cartId === "null" || !cart.cartId) {
      try {
        const res = await apiCreateCart();
        const newCart: Cart = {
          cartId: res.cartId,
          items: cart?.items ?? []
        };
        setCartLocal(newCart);
        return res.cartId;

      } catch (error) {
        console.error("Failed to create cart", error);
        return null;
      }
    }
    return cart.cartId;
  }


  async function addOrUpdateItem(item: CartItem) {
    const cid = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    // Keep cart state for rollback
    const prevCart = cart;

    // Update item in cart items list if present, append otherwise
    const currentItems = prevCart?.items ?? [];
    let found = false;
    const newItems = currentItems.map((i) => {
      if (i.eventId === item.eventId) {
        found = true;
        return item;
      }
      return i;
    });
    if (!found)
      newItems.push(item);

    const newCart: Cart = { cartId: cid, items: newItems };

    // Optimistic update to local state and localStorage
    setCartLocal(newCart);
    console.log("addOrUpdateItem > Updated local cart:", newCart);

    // Persist to backend; rollback on error
    try {
      await apiSaveCartItem(cid, item);
      console.log("addOrUpdateItem > Successfully saved cart item to backend");

    } catch (error) {
      console.error("apiSaveCartItem failed — rolling back", error);
      if (prevCart) {
        setCartLocal(prevCart);
      } else {
        setCart(null);
        localStorage.removeItem("cart");
      }
      throw error; // rethrow to pass to user
    }
  }

  async function checkout(request: CheckoutRequest) {
    const cid = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    try {
      await apiCheckoutCart(cid, request);
      console.log("Checkout successful");

    } catch (error) {
      console.error("checkout failed", error);
      throw error; // rethrow to pass to user
    }
  }


  const value: CartContextType = {
    cart,
    setCartLocal,
    addOrUpdateItem,
    // removeItem,
    // refreshFromServer,
    checkout,
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}

// Custom hook with guard against using the context outside of a provider
export function useCart(): CartContextType {
  const ctx = useContext(CartContext);
  if (ctx === undefined)
    throw new Error("useCart must be used within CartProvider");
  return ctx;
}
