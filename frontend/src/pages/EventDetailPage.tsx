
import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';

import queryClient from '@/config/queryClient';

import TicketQuantitySelector from '@/components/TicketQuantitySelector';
import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchEventById } from '@/api/catalog';
import { imageBaseUrl } from '@/utils/globals';

import type { Event } from '@/types/catalog';
import type { CartItem } from '@/types/cart';
import { useCart } from '@/context/CartContext';

function EventDetailPage() {
  const { addOrUpdateItem } = useCart();

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
    console.log("EventDetailPage > Add to cart clicked");

    // Save item to local cart and backend
    try {
      let item: CartItem = {
        eventId: id,
        ticketCount: ticketCount
      }
      console.log("EventDetailPage > Saving cart item:", item);

      await addOrUpdateItem(item);

    } catch (error) {
      console.error("EventDetailPage > Error saving cart:", error);
      // TODO: Show error to user
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
            <div className='d-flex align-items-center gap-5 my-3'>
              <TicketQuantitySelector
                ticketCount={ticketCount}
                setTicketCount={setTicketCount}
              />
              <button className="px-4 py-2 bg-back-300 text-compl-300 border-2 border-compl-300" onClick={saveItemToCart}>Add to Cart</button>
            </div>
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