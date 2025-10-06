import { useState, useEffect } from 'react'
import axios from 'axios';
import { Link } from "react-router-dom";

import type { Venue, Event } from '@/types/catalog';
import EventList from '@/components/EventList';
import VenueList from '@/components/VenueList';

function HomePage() {
  const [allVenues, setAllVenues] = useState<Venue[]>([]);
  const [allEvents, setAllEvents] = useState<Event[]>([]);

  const baseURL = import.meta.env.VITE_API_BASE_URL;
  if (!baseURL) {
    throw new Error("VITE_API_BASE_URL is not defined");
  }

  useEffect(() => {

    const getVenues = () => {
      let url = `${baseURL}/venues`;
      console.log("axios get url: ", url);
      axios.get(url, { withCredentials: true })
        .then(response => {
          console.log("response.data: ", response.data);
          setAllVenues(response.data);
        })
        .catch(error => {
          console.error('There was an error making the request', error);
        });
    }
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
    getVenues();
    getEvents();

  }, []);

  return (
    <>
      {allEvents.length > 0 ? (
        <div>
          <Link to="/events">Upcoming events: {allEvents.length}</Link>

          <EventList events={allEvents} />
        </div>
      ) : (
        <p>No events to display.</p>
      )}

      {allVenues.length > 0 ? (
        <div>
          <Link to="/venues">Browse venues: {allVenues.length}</Link>

          <VenueList venues={allVenues} />
        </div>
      ) : (
        <p>No venues to display.</p>
      )}
    </>
  )
}

export default HomePage
