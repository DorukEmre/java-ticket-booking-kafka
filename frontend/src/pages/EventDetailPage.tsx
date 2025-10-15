
import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';

import queryClient from '@/config/queryClient';

import TicketQuantitySelector from '@/components/TicketQuantitySelector';
import ApiErrorMessage from '@/components/ApiErrorMessage';
import ActionButton from '@/components/ActionButton';

import { fetchEventById } from '@/api/catalog';
import { CartStatus, imageBaseUrl } from '@/utils/globals';
import { useCart } from "@/hooks/useCart";
import useDocumentTitle from '@/hooks/useDocumentTitle';
import type { Event } from '@/types/catalog';
import type { CartItem } from '@/types/cart';
import { addToCartIcon, arrowBackIcon } from "@/assets";
import LoadingSpinner from '@/components/LoadingSpinner';


function EventDetailPage() {

  const { cart, addOrUpdateItem, deleteCartAndUpdateItem } = useCart();

  const [ticketCount, setTicketCount] = useState<number>(1);

  const { eventId } = useParams<{ eventId: string }>();
  const id = Number(eventId);


  // Use cached data first, then fetch
  const eventQuery = useQuery<Event>({
    queryKey: ["event", id],
    queryFn: () => fetchEventById(id),
    initialData: () => {
      const events = queryClient.getQueryData<Event[]>(["events"]);
      return events?.find((ev) => ev.id === id);
    },
  });

  const { data: event, isLoading, isError, error } = eventQuery;

  useDocumentTitle(event ? `${event.name} | Ticket Booking` : "Ticket Booking");

  // Displayed ticket count is synced with cart
  useEffect(() => {

    if (cart && cart.items) {

      const existingItem = cart.items.find(item => item.eventId === id);

      if (existingItem) {
        setTicketCount(existingItem.ticketCount);
      } else {
        setTicketCount(1);
      }
    }

  }, [cart, cart?.items]);

  async function saveItemToCart() {
    console.log("EventDetailPage > Add to cart clicked");

    let item: CartItem = {
      eventId: id,
      ticketCount: ticketCount,
      ticketPrice: event ? event.ticketPrice : 0,
    }
    console.log("EventDetailPage > Saving cart item:", item);

    // Save item to local cart and backend
    try {

      if (cart?.status == CartStatus.INVALID
        || cart?.status == CartStatus.CONFIRMED
      ) {
        await deleteCartAndUpdateItem(item);
      } else {
        await addOrUpdateItem(item);
      }

    } catch (error) {
      console.error("EventDetailPage > Error saving cart:", error);
      // TODO: Show error to user
    }
  }

  return (
    <>
      <section>
        {isLoading && <LoadingSpinner />}

        {isError && <ApiErrorMessage error={error} />}

        {!isLoading && !isError && event && (
          <div className="card border-0 bg-transparent mb-3" style={{ maxWidth: '800px' }}>
            <div className="row g-4">

              {event.imageUrl && (
                <div className="col-md-5">
                  <img
                    className="card-img"
                    src={imageBaseUrl + event.imageUrl}
                    alt={event.name}
                    style={{ maxWidth: '400px', height: 'auto' }}
                  />
                </div>
              )}

              <div className="col-md-7">
                <div className="card-body p-0">

                  <div className='text-neutral-300'>
                    <h1 className='mb-4'>{event.name}, {event.id}</h1>
                    <p>Location: {event.venue.location}</p>
                    <p>Total Capacity: {event.venue.totalCapacity}</p>
                    <p>Event Date: {new Date(event.eventDate).toLocaleString()}</p>
                    <p>Ticket Price: {event.ticketPrice.toFixed(2)}</p>
                    <p>Description: {event.description}</p>
                  </div>

                  <div className='d-flex align-items-center gap-5 my-3 justify-content-end'>
                    <TicketQuantitySelector
                      ticketCount={ticketCount}
                      setTicketCount={setTicketCount}
                    />
                    <div
                      role="button"
                      tabIndex={0}
                      onClick={saveItemToCart}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          saveItemToCart();
                        }
                      }}
                    >
                      {cart && cart.items && cart.items.find(item => item.eventId === id) ? (
                        <ActionButton text="Update Cart" icon={addToCartIcon} />
                      ) : (
                        <ActionButton text="Add to Cart" icon={addToCartIcon} />
                      )}
                    </div>
                  </div>

                </div>
              </div>

            </div>
          </div>
        )}

        {!isLoading && !isError && !event && (
          <p>Event not found.</p>
        )}

        <Link to={"/events"} className='icon-link mt-5'>
          <img src={arrowBackIcon} aria-hidden="true" />
          Back to events
        </Link >

      </section>
    </>
  )

}

export default EventDetailPage;