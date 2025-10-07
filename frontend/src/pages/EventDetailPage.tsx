import { useQuery } from '@tanstack/react-query';

import type { Event } from '@/types/catalog';

import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchEventById } from '@/api/catalog';
import { useParams } from 'react-router-dom';
import queryClient from '@/config/queryClient';
import { imageBaseUrl } from '@/utils/utils';

function EventDetailPage() {
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
  console.log(event);
  console.log(error);

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