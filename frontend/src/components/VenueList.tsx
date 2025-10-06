import type { Venue } from '@/types/catalog';

type VenueListProps = {
  venues: Venue[];
};

function VenueList({ venues }: VenueListProps) {
  return (
    <ul>
      {venues.map(venue => (
        <li key={venue.venueId}>
          Venue ID {venue.venueId}: {venue.name}, {venue.location} - {venue.totalCapacity}
        </li>
      ))}
    </ul>
  )

}

export default VenueList;