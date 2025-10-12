import VenueCard from '@/components/VenueCard';

import type { Venue } from '@/types/catalog';

function VenueList({ venues }: { venues: Venue[] }) {

  // Sort 6 venues randomly
  const displayedEvents = [...venues]
    .sort(() => (0.5 - Math.random()))
    .slice(0, 6);

  const colours = [
    { background: 'linear-gradient(90deg, rgba(10, 102, 163, 0.9) 0%, rgba(101, 149, 211, 0.9) 100%)' },
    { background: 'linear-gradient(90deg, rgba(12, 175, 80, 0.9) 0%, rgba(65, 171, 109, 0.9) 100%)' },
    { background: 'linear-gradient(90deg, rgba(150, 68, 183, 0.9) 0%, rgba(161, 129, 174, 0.9) 100%)' },
    { background: 'linear-gradient(90deg, rgba(243, 156, 18, 0.9) 0%, rgba(214, 174, 95, 0.9) 100%)' },
    { background: 'linear-gradient(90deg, rgba(9, 160, 130, 0.9) 0%, rgba(87, 161, 147, 0.9) 100%)' },
    { background: 'linear-gradient(90deg, rgba(231, 62, 43, 0.9) 0%, rgba(197, 109, 99, 0.9) 100%)' },
  ];

  // create a new array with the colours  randomly ordered
  const shuffledColours = [...colours].sort(() => 0.5 - Math.random());

  return (
    <ul className="d-flex list-unstyled flex-wrap justify-content-center justify-content-md-between align-items-stretch gap-4">
      {displayedEvents.map(venue => (
        <VenueCard
          key={venue.venueId}
          venue={venue}
          overlayColour={shuffledColours[displayedEvents.indexOf(venue)]}
        />
      ))}
    </ul>
  )
}

export default VenueList;