import { useQuery } from '@tanstack/react-query';

import type { Venue } from '@/types/catalog';

import VenueList from '@/components/VenueList';
import ApiErrorMessage from '@/components/ApiErrorMessage';

import { fetchVenues } from '@/api/catalog';
import useDocumentTitle from '@/hooks/useDocumentTitle';

function VenuesPage() {
  useDocumentTitle("Venues | Ticket Booking");

  // Fetch venues
  const {
    data: venues,
    isLoading,
    isError,
    error,
  } = useQuery<Venue[]>({
    queryKey: ["venues"],
    queryFn: fetchVenues,
  });

  return (
    <>
      <section>
        <p>Browse venues:</p>

        {isLoading && <p>Loading venues...</p>}

        {isError && <ApiErrorMessage error={error} />}

        {!isLoading && !isError && venues && venues.length > 0 && (
          <VenueList venues={venues} />
        )}

        {!isLoading && !isError && venues && venues.length === 0 && (
          <p>No venues to display.</p>
        )}

      </section>
    </>
  )
}

export default VenuesPage
