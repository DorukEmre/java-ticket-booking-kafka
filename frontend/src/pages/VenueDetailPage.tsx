import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';

import queryClient from '@/config/queryClient';

import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchEvents, fetchVenueById } from '@/api/catalog';
import { imageBaseUrl } from '@/utils/globals';
import useDocumentTitle from '@/hooks/useDocumentTitle';
import type { Event, Venue } from '@/types/catalog';
import { arrowBackIcon } from "@/assets";
import EventCard from '@/components/EventCard';


function VenueDetailPage() {

  const { venueId } = useParams<{ venueId: string }>();
  const id = Number(venueId);

  // Use cached data first, then fetch
  const venueQuery = useQuery<Venue>({
    queryKey: ["venue", id],
    queryFn: () => fetchVenueById(id),
    initialData: () => {
      const venues = queryClient.getQueryData<Venue[]>(["venues"]);
      return venues?.find((ven) => ven.id === id);
    },
  });

  const eventsQuery = useQuery<Event[]>({
    queryKey: ["events", id],
    queryFn: () => fetchEvents()
      .then(all => all.filter(e => e.venue.id === id)),
    initialData: () => {
      const events = queryClient.getQueryData<Event[]>(["events"]);
      return events?.filter(e => e.venue.id === id);
    },
  });


  const {
    data: venue, isLoading: venueLoading,
    isError: venueError, error: venueErrorObj,
  } = venueQuery;

  const {
    data: events, isLoading: eventsLoading,
    isError: eventsError, error: eventsErrorObj,
  } = eventsQuery;

  console.log(venue);
  console.log(venueError);
  console.log(events);

  useDocumentTitle(venue ? `${venue.name} | Ticket Booking` : "Ticket Booking");


  return (
    <>
      <section>
        {venueLoading && <p>Loading venues...</p>}

        {venueError && <ApiErrorMessage error={venueErrorObj} />}

        {!venueLoading && !venueError && venue && (
          <>
            <div className="card border-0 bg-transparent mb-3" style={{ maxWidth: '800px' }}>
              <div className="row g-4">

                {venue.imageUrl && (
                  <div className="col-md-6">
                    <img
                      className="card-img"
                      src={imageBaseUrl + venue.imageUrl}
                      alt={venue.name}
                      style={{ maxWidth: '400px', height: 'auto' }}
                    />
                  </div>
                )}

                <div className="col-md-6">
                  <div className="card-body p-0">

                    <div className='text-neutral-300'>
                      <h2>{venue.name}, {venue.id}</h2>
                      <p>{venue.location}</p>
                      <p>Total Capacity: {venue.totalCapacity}</p>
                    </div>

                  </div>
                </div>

              </div>
            </div>
          </>
        )}

        {!venueLoading && !venueError && !venue && (
          <p>Venue not found.</p>
        )}


        {eventsError && <ApiErrorMessage error={eventsErrorObj} />}

        {!eventsLoading && !eventsError && events && (
          <>
            <div>
              <h3 className='mt-5 mb-4 fs-5'>Upcoming Events at this venue:</h3>
              {events && events.length > 0 ? (
                <ul className='d-flex list-unstyled flex-wrap justify-content-center justify-content-md-between align-items-stretch gap-4'>
                  {events.map((event) => (

                    <EventCard
                      key={event.id}
                      event={event}
                    />

                  ))}
                </ul>
              ) : (
                <p>No upcoming events at this venue.</p>
              )}
            </div>
          </>
        )}

        <Link to={"/venues"} className='icon-link mt-5'>
          <img src={arrowBackIcon} aria-hidden="true" />
          Back to venues
        </Link>

      </section>
    </>
  )

}

export default VenueDetailPage
