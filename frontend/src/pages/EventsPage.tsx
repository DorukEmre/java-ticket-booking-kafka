import { useState, useEffect } from 'react'
import axios from 'axios';

import type { Event } from '@/types/catalog';
import EventList from '@/components/EventList';

function EventsPage() {
  const [allEvents, setAllEvents] = useState<Event[]>([]);

  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  useEffect(() => {

    const getEvents = () => {
      let url = `${baseURL}/events`;
      console.log("axios get url: ", url);
      axios.get(url, { withCredentials: true })
        .then(response => {
          console.log("response.data: ", response.data);
          setAllEvents(response.data);
        })
        .catch(error => {
          console.error('There was an error making the request', error);
        });
    }
    getEvents();

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
