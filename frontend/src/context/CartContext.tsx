import type { Cart, CartItem, CheckoutRequest } from "@/types/cart";
import { createContext, useContext, useEffect, useState, useMemo, type ReactNode } from "react";
import {
  apiCreateCart,
  apiSaveCartItem,
  apiDeleteCartItem,
  apiDeleteCart,
  apiCheckoutCart,
} from "@/api/cart";

type CartContextType = {
  cart: Cart | null;
  setCartLocal: (c: Cart) => void;
  addOrUpdateItem: (item: CartItem) => Promise<void>;
  removeItem: (item: CartItem) => Promise<void>;
  deleteCart: () => Promise<void>;
  // refreshFromServer: () => Promise<void>;
  checkout: (request: CheckoutRequest) => Promise<void>;
  totalPrice: number;

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


  // Create cart and save it if none present, return cart or newCart
  async function ensureCartId(): Promise<{ cartId: string | null, cartObj: Cart | null }> {

    if (!cart || cart.cartId === "null" || !cart.cartId) {

      try {
        const res = await apiCreateCart();
        const newCart: Cart = {
          cartId: res.cartId,
          items: cart?.items ?? [],
          status: cart?.status ?? "IN_PROGRESS"
        };
        setCartLocal(newCart);
        return { cartId: res.cartId, cartObj: newCart };

      } catch (error) {
        console.error("Failed to create cart", error);
        return { cartId: null, cartObj: null };
      }
    }

    return { cartId: cart.cartId, cartObj: cart };
  }


  async function addOrUpdateItem(item: CartItem) {
    const { cartId: cid, cartObj: prevCart } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");


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

    const newCart: Cart =
      { cartId: cid, items: newItems, status: prevCart!.status };

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
    const { cartId: cid } = await ensureCartId();
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

  async function removeItem(item: CartItem) {
    const { cartId: cid, cartObj: prevCart } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    // check item to delete exists
    const existing = cart?.items.find(i => i.eventId === item.eventId);
    if (!existing)
      return;

    // Build new items array without the item
    const newItems = (prevCart?.items ?? []).filter(i => i.eventId !== item.eventId);

    const newCart: Cart = {
      cartId: cid, items: newItems, status: prevCart?.status ?? "IN_PROGRESS",
    };

    // Optimistic update to local state and localStorage
    setCartLocal(newCart);
    console.log("addOrUpdateItem > Updated local cart:", newCart);

    try {
      await apiDeleteCartItem(cid, item);

    } catch (error) {
      console.error("removeItem > apiDeleteCartItem failed — rolling back", error);
      if (prevCart) {
        setCartLocal(prevCart);
      } else {
        setCart(null);
        localStorage.removeItem("cart");
      }
      throw error; // rethrow to pass to user      
    }
  }

  async function deleteCart() {
    const { cartId: cid } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    localStorage.removeItem("cart");
    setCart(null);

    console.log("deleteCart > delete carrId:", cid);

    try {
      await apiDeleteCart(cid);

    } catch (error) {
      console.error("Failed to delete cart from backend", error);
      throw error; // rethrow to pass to user
    }
  }

  const totalPrice = useMemo(() => {
    if (!cart?.items?.length)
      return 0;

    let total = cart.items.reduce((sum, it) => {
      const price = Number(it.ticketPrice ?? 0);
      const qty = Number(it.ticketCount ?? 1);
      return sum + price * qty;
    }, 0);

    return total;

  }, [cart, cart?.items]);

  const value: CartContextType = {
    cart,
    setCartLocal,
    addOrUpdateItem,
    removeItem,
    deleteCart,
    // refreshFromServer,
    checkout,
    totalPrice,
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
