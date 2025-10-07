import type { Venue } from '@/types/catalog';
import { Link } from 'react-router-dom';

type VenueListProps = {
  venues: Venue[];
};

function VenueList({ venues }: VenueListProps) {
  return (
    <ul>
      {venues.map(venue => (
        <li key={venue.venueId}>
          <Link to={`/venues/${venue.venueId}`}>
            Venue ID {venue.venueId}: {venue.name}, {venue.location} - {venue.totalCapacity}
          </Link>
        </li>
      ))}
    </ul>
  )

}

export default VenueList;