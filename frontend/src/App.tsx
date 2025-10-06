import { useState, useEffect } from 'react'
import axios from 'axios';
import './App.css'

type Venue = {
  venueId: number;
  name: string;
  location: string;
  totalCapacity: number;
};

type Event = {
  eventId: number;
  name: string;
  date: string; // ISO format date string
  venueId: number;
};

function App() {
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
          <p>Upcoming events: {allVenues.length}</p>
          <ul>
            {allEvents.map(event => (
              <li key={event.eventId}>
                Event ID {event.venueId}: {event.name} on {new Date(event.date).toLocaleDateString()}
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p>No events to display.</p>
      )}

      {allVenues.length > 0 ? (
        <div>
          <p>Browse venues: {allVenues.length}</p>
          <ul>
            {allVenues.map(venue => (
              <li key={venue.venueId}>
                Venue ID {venue.venueId}: {venue.name}, {venue.location} - {venue.totalCapacity}
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p>No venues to display.</p>
      )}
    </>
  )
}

export default App
