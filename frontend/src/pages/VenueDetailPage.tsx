import { useQuery } from '@tanstack/react-query';

import type { Venue } from '@/types/catalog';

import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchVenueById } from '@/api/catalog';
import { useParams } from 'react-router-dom';
import queryClient from '@/config/queryClient';
import { imageBaseUrl } from '@/utils/utils';

function VenueDetailPage() {
  const { venueId } = useParams<{ venueId: string }>();
  const id = Number(venueId);

  // Use cached data first, then fetch
  const venueQuery = useQuery<Venue>({
    queryKey: ["venue", id],
    queryFn: () => fetchVenueById(id),
    initialData: () => {
      const venues = queryClient.getQueryData<Venue[]>(["venues"]);
      return venues?.find((v) => v.venueId === id);
    },
  });

  const { data: venue, isLoading, isError, error } = venueQuery;
  console.log(venue);
  console.log(error);

  return (
    <>
      <section>
        <p>Browse venues:</p>

        {isLoading && <p>Loading venues...</p>}

        {isError && <ApiErrorMessage error={error} />}

        {!isLoading && !isError && venue && (
          <div>
            <h2>{venue.name}, {venue.venueId}</h2>
            <p>Location: {venue.location}</p>
            <p>Total Capacity: {venue.totalCapacity}</p>
            {venue.imageUrl && (
              <img
                src={imageBaseUrl + venue.imageUrl}
                alt={venue.name}
                style={{ maxWidth: '300px', height: 'auto' }}
              />
            )}
          </div>
        )}

        {!isLoading && !isError && !venue && (
          <p>Venue not found.</p>
        )}

      </section>
    </>
  )

}

export default VenueDetailPage
