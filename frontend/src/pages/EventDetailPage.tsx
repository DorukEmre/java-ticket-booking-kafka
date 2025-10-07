
import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';

import queryClient from '@/config/queryClient';

import TicketQuantitySelector from '@/components/TicketQuantitySelector';
import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchEventById } from '@/api/catalog';
import { createCart, saveCartItem } from '@/api/cart';
import { imageBaseUrl } from '@/utils/utils';

import type { Event } from '@/types/catalog';
import type { Cart, CartItem } from '@/types/cart';

function EventDetailPage() {
  const [ticketCount, setTicketCount] = useState<number>(1);
  const { eventId } = useParams<{ eventId: string }>();
  const id = Number(eventId);

  // Use cached data first, then fetch
  const eventQuery = useQuery<Event>({
    queryKey: ["event", id],
    queryFn: () => fetchEventById(id),
    initialData: () => {
      const events = queryClient.getQueryData<Event[]>(["events"]);
      return events?.find((v) => v.eventId === id);
    },
  });

  const { data: event, isLoading, isError, error } = eventQuery;

  async function saveItemToCart() {
    console.log("Add to cart clicked");

    // Get existing cart from localStorage
    let existingCart = localStorage.getItem("cart");

    let cart: Cart = existingCart
      ? JSON.parse(existingCart)
      : { cartId: "null", items: [] };

    console.log("existing cartId:", existingCart);

    // If no cartId, create a new cart
    if (cart.cartId === "null") {
      try {
        let response = await createCart();

        if (response && response.cartId) {
          cart.cartId = response.cartId;
          localStorage.setItem("cart", JSON.stringify(cart));
          console.log("Cart ID saved to localStorage:", cart.cartId);
        }

      } catch (error) {
        console.error("Error creating cart:", error);
        // Error creating cart: Object { status: 500, message: "Unable to connect to Redis" }
        return;
      }
    }

    // Save item to local cart and backend
    try {

      // Create cart item
      let item: CartItem = {
        eventId: id,
        ticketCount: ticketCount
      }
      console.log("Saving cart item:", item);

      // Replace or add item in local cart
      const existingItemIndex = cart.items.findIndex(item => item.eventId === id);
      if (existingItemIndex !== -1) {
        cart.items[existingItemIndex].ticketCount = ticketCount;
      } else {
        cart.items.push({ eventId: id, ticketCount: ticketCount });
      }
      localStorage.setItem("cart", JSON.stringify(cart));
      console.log("Cart item saved to localStorage stringify:", JSON.stringify(cart));

      // Save item to backend
      let response = await saveCartItem(cart.cartId, item);
      console.log("saveCartItem response:", response);

    } catch (error) {
      console.error("Error saving cart:", error);

    }
  }

  return (
    <>
      <section>
        <p>Browse events:</p>

        {isLoading && <p>Loading events...</p>}

        {isError && <ApiErrorMessage error={error} />}

        {!isLoading && !isError && event && (
          <div>
            <h2>{event.name}, {event.eventId}</h2>
            <p>Location: {event.venue.location}</p>
            <p>Total Capacity: {event.venue.totalCapacity}</p>
            <p>Event Date: {new Date(event.eventDate).toLocaleString()}</p>
            <p>Ticket Price: ${event.ticketPrice.toFixed(2)}</p>
            <p>Description: {event.description}</p>
            {event.imageUrl && (
              <img
                src={imageBaseUrl + event.imageUrl}
                alt={event.name}
                style={{ maxWidth: '300px', height: 'auto' }}
              />
            )}
            <TicketQuantitySelector
              ticketCount={ticketCount}
              setTicketCount={setTicketCount}
            />
            <button className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300" onClick={saveItemToCart}>Add to Cart</button>
          </div>
        )}

        {!isLoading && !isError && !event && (
          <p>Event not found.</p>
        )}

      </section>
    </>
  )

}

export default EventDetailPage;