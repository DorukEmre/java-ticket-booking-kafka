import { useState, useEffect } from 'react'

import type { Event } from '@/types/catalog';
import EventList from '@/components/EventList';
import { fetchEvents } from '@/api/events';

function EventsPage() {
  const [allEvents, setAllEvents] = useState<Event[]>([]);

  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  useEffect(() => {

    fetchEvents()
      .then(setAllEvents)
      .catch(error => {
        console.error('There was an error making the request', error);
      });

  }, []);

  return (
    <>
      {allEvents.length > 0 ? (
        <div>
          <p>Upcoming events: {allEvents.length}</p>

          <EventList events={allEvents} />
        </div>
      ) : (
        <p>No events to display.</p>
      )}
    </>
  )
}

export default EventsPage
