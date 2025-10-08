import { CartContext } from "@/context/CartContext";
import type { CartContextType } from "@/types/cart";
import { useContext } from "react";

// Custom hook with guard against using the context outside of a provider
export function useCart(): CartContextType {
  const ctx = useContext(CartContext);
  if (ctx === undefined)
    throw new Error("useCart must be used within CartProvider");
  return ctx;
}
