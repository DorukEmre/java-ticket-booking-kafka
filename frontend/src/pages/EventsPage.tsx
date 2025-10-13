import { useQuery } from '@tanstack/react-query';

import type { Event } from '@/types/catalog';

import EventList from '@/components/EventList';
import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchEvents } from '@/api/catalog';
import useDocumentTitle from '@/hooks/useDocumentTitle';
import LoadingSpinner from '@/components/LoadingSpinner';

function EventsPage() {
  useDocumentTitle("Events | Ticket Booking");

  // Fetch events
  const {
    data: events,
    isLoading,
    isError,
    error,
  } = useQuery<Event[]>({
    queryKey: ["events"],
    queryFn: fetchEvents,
  });

  return (
    <>
      <section>
        <h1 className='mb-4'>Events</h1>

        {isLoading && <LoadingSpinner />}

        {isError && <ApiErrorMessage error={error} />}

        {!isLoading && !isError && events && events.length > 0 && (
          <EventList events={events} />
        )}

        {!isLoading && !isError && events && events.length === 0 && (
          <p>No events to display.</p>
        )}

      </section>
    </>
  )
}

export default EventsPage
