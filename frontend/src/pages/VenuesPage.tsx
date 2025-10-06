import { useState, useEffect } from 'react'

import type { Venue } from '@/types/catalog';
import VenueList from '@/components/VenueList';
import { fetchVenues } from '@/api/venues';

function VenuesPage() {
  const [allVenues, setAllVenues] = useState<Venue[]>([]);


  useEffect(() => {

    fetchVenues()
      .then(setAllVenues)
      .catch(error => {
        console.error('There was an error making the request', error);
      });

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
