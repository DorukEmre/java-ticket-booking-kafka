import type { CSSProperties } from 'react';

import VenueCard from '@/components/VenueCard';

import type { Venue } from '@/types/catalog';

function VenueList({ venues }: { venues: Venue[] }) {

  function offset(): number {
    return Math.floor(Math.random() * 40) - 20;
  }

  function generateBackgroundColour(): CSSProperties {

    let hue = Math.random() * 360;

    let colour = { background: `linear-gradient(110deg, hsla(${hue}, 80%, 40%, 0.90) 0%, hsla(${hue + offset()}, 40%, 40%, 0.90) 100%)` };

    return colour;
  }

  return (
    <ul className="d-flex list-unstyled flex-wrap justify-content-center justify-content-md-between align-items-stretch gap-4">
      {venues.map(venue => (
        <VenueCard
          key={venue.id}
          venue={venue}
          overlayColour={generateBackgroundColour()}
        />
      ))}
    </ul>
  )
}

export default VenueList;