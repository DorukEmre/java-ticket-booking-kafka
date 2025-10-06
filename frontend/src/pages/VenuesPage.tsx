import { useState, useEffect } from 'react'
import axios from 'axios';

import type { Venue } from '@/types/catalog';
import VenueList from '@/components/VenueList';

function VenuesPage() {
  const [allVenues, setAllVenues] = useState<Venue[]>([]);

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
    getVenues();

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
