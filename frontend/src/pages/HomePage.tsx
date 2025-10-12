import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import EventList from '@/components/EventList';
import VenueList from '@/components/VenueList';

import { fetchEvents, fetchVenues } from '@/api/catalog';

import type { Venue, Event } from '@/types/catalog';
import ApiErrorMessage from "@/components/ApiErrorMessage";

function HomePage() {
  // Fetch events
  const {
    data: events,
    isLoading: eventsLoading,
    isError: eventsError,
    error: eventsErrorObj,
  } = useQuery<Event[]>({ queryKey: ["events"], queryFn: fetchEvents, });

  // Fetch venues
  const {
    data: venues,
    isLoading: venuesLoading,
    isError: venuesError,
    error: venuesErrorObj,
  } = useQuery<Venue[]>({ queryKey: ["venues"], queryFn: fetchVenues, });

  // Sort 6 venues randomly
  const venuesToDisplay = [...(venues ?? [])]
    .sort(() => 0.5 - Math.random())
    .slice(0, 6);

  return (
    <>
      <h1 className="visually-hidden">Home</h1>

      <section className="pb-5">
        <h2 className="section__title mb-5">
          <Link to="/events">Upcoming events</Link>
        </h2>

        {eventsLoading && <p>Loading events...</p>}

        {eventsError && <ApiErrorMessage error={eventsErrorObj} />}

        {!eventsLoading && !eventsError && events && events.length > 0 && (
          <EventList events={events} />
        )}

        {!eventsLoading && !eventsError && events && events.length === 0 && (
          <p>No events to display.</p>
        )}

      </section>

      <section className="mt-5">
        <h2 className="section__title mb-5">
          <Link to="/venues">Browse venues</Link>
        </h2>

        {venuesLoading && <p>Loading venues...</p>}

        {venuesError && <ApiErrorMessage error={venuesErrorObj} />}

        {!venuesLoading && !venuesError && venues && venues.length > 0 && (
          <VenueList venues={venuesToDisplay} />
        )}

        {!venuesLoading && !venuesError && venues && venues.length === 0 && (
          <p>No venues to display.</p>
        )}

      </section>

    </>
  )
}

export default HomePage
