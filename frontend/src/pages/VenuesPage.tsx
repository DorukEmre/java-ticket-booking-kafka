import { useState, useEffect } from 'react'

import type { Venue } from '@/types/catalog';
import VenueList from '@/components/VenueList';
import { fetchVenues } from '@/api/catalog';

function VenuesPage() {
  const [allVenues, setAllVenues] = useState<Venue[]>([]);


  useEffect(() => {

    async function loadVenues() {
      try {
        const events = await fetchVenues();
        setAllVenues(events);
      } catch (error) {
        console.error('There was an error making the request', error);
      }
    }
    loadVenues();

  }, []);

  return (
    <>
      {allVenues.length > 0 ? (
        <div>
          <p>Browse venues: {allVenues.length}</p>

          <VenueList venues={allVenues} />
        </div>
      ) : (
        <p>No venues to display.</p>
      )}
    </>
  )
}

export default VenuesPage
