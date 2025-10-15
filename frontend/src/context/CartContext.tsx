import type { Cart, CartContextType, CartItem, CheckoutRequest } from "@/types/cart";
import { createContext, useEffect, useState, useMemo, type ReactNode } from "react";
import {
  apiCreateCart,
  apiSaveCartItem,
  apiDeleteCartItem,
  apiDeleteCart,
  apiCheckoutCart,
  apiFetchCartById,
} from "@/api/cart";
import { CartStatus } from "@/utils/globals";


export const CartContext = createContext<CartContextType | undefined>(undefined);

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
  async function ensureCartId(): Promise<{ cartId: string, cartObj: Cart | null }> {

    if (!cart || cart.cartId === "null" || !cart.cartId) {

      try {
        const res = await apiCreateCart();
        const newCart: Cart = {
          cartId: res.cartId,
          items: cart?.items ?? [],
          status: cart?.status ?? CartStatus.PENDING,
        };
        setCartLocal(newCart);
        return { cartId: res.cartId, cartObj: newCart };

      } catch (error) {
        console.error("Failed to create cart", error);
        throw new Error("Failed to create cart");
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

  async function deleteCartAndUpdateItem(item: CartItem) {
    const { cartId: cid, cartObj: prevCart } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    // Get current items and add or update the item
    const currentItems = prevCart?.items ?? [];
    let found = false;
    const newItems = currentItems
      .filter(i => !i.unavailable)
      .map((i) => {
        if (i.eventId === item.eventId) {
          found = true;
          return item;
        }
        return i;
      });
    if (!found)
      newItems.push(item);

    // delete local cart
    localStorage.removeItem("cart");
    console.log("deleteCart > delete cartId:", cid);

    // delete backend cart
    try {
      await apiDeleteCart(cid);

    } catch (error) {
      console.error("Failed to delete cart from backend", error);
      throw error; // rethrow to pass to user
    }


    // create new cart with updated item
    try {
      const res = await apiCreateCart();
      const newCart: Cart = {
        cartId: res.cartId,
        items: newItems,
        status: CartStatus.PENDING,
      };
      setCartLocal(newCart);

    } catch (error) {
      console.error("Failed to create cart", error);
      throw new Error("Failed to create cart");
    }

  }

  async function proceedToCheckout() {
    let { cartId: cid, cartObj: prevCart } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    console.log("proceedToCheckout > Proceeding to checkout with cart:", cart);

    try {

      let request: CheckoutRequest = {
        items: cart
          ? cart.items
            .filter(item => !item.unavailable)
            .map(item => {
              if (item.priceChanged) {
                let newItem: CartItem = {
                  eventId: item.eventId,
                  ticketCount: item.ticketCount,
                  ticketPrice: item.ticketPrice,
                  priceChanged: false,
                  previousPrice: item.ticketPrice,
                  unavailable: false
                };
                return newItem;
              }
              return item;
            })
          : []
      };

      console.log("proceedToCheckout > cart items after filtering:", request.items);
      console.log("proceedToCheckout > Checkout prevCart?.status:", prevCart?.status);
      if (prevCart?.status === CartStatus.INVALID) {
        await deleteCart();
        try {
          const res = await apiCreateCart();
          cid = res.cartId;
          const newCart: Cart = {
            cartId: cid,
            items: request.items,
            status: CartStatus.PENDING
          };
          setCartLocal(newCart);
          prevCart = newCart;
        } catch (err) {
          throw new Error("No cartId available after deleting invalid cart");
        }
      }

      await apiCheckoutCart(cid, request);
      console.log("Checkout successful");

      // Checkout successful, set cart status to IN_PROGRESS
      if (cart) {
        const updatedCart = {
          cartId: cid,
          items: request.items, // unavailable items filtered out at checkout
          status: CartStatus.IN_PROGRESS
        };
        setCartLocal(updatedCart);
      }

    } catch (error) {
      console.error("checkout failed", error);

      if (cart) {
        const updatedCart = { ...cart, status: CartStatus.FAILED };
        setCartLocal(updatedCart);
      }

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
      cartId: cid, items: newItems, status: prevCart?.status ?? CartStatus.PENDING,
    };

    // Optimistic update to local state and localStorage
    setCartLocal(newCart);
    console.log("addOrUpdateItem > Updated local cart:", newCart);

    try {
      await apiDeleteCartItem(cid, item);

    } catch (error: any) {
      if (error?.status === 404 && error?.message?.includes("Item not found in cart")) {
        // item was not found on backend, so we can consider it deleted
        return;
      }

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

    console.log("deleteCart > delete cartId:", cid);

    try {
      await apiDeleteCart(cid);

    } catch (error) {
      console.error("Failed to delete cart from backend", error);
      throw error; // rethrow to pass to user
    }
  }

  async function refreshFromServer() {
    const { cartId: cid, cartObj: prevCart } = await ensureCartId();
    if (!cid)
      throw new Error("No cartId available");

    try {
      const response = await apiFetchCartById(cid);

      if (response.status && prevCart && response.status !== prevCart.status) {
        const updatedCart = {
          ...prevCart,
          status: response.status,
          items: response.items
        };
        setCartLocal(updatedCart);
        console.log("refreshFromServer > Updated cart from server:", updatedCart);
      }
      return response;

    } catch (error) {
      console.error("refreshFromServer > Failed to fetch cart status", error);
      throw error; // rethrow to pass to user
    }
  }

  const totalPrice = useMemo(() => {
    if (!cart?.items?.length)
      return 0;

    let total = cart.items
      .filter(item => !item.unavailable)
      .reduce((sum, it) => {
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
    deleteCartAndUpdateItem,
    removeItem,
    deleteCart,
    refreshFromServer,
    proceedToCheckout,
    totalPrice,
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}
