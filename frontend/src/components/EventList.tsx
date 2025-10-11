import EventCard from '@/components/EventCard';

import type { Event } from '@/types/catalog';

function EventList({ events }: { events: Event[] }) {

  return (
    <ul className="d-flex list-unstyled flex-wrap justify-content-center justify-content-md-between align-items-stretch gap-4">
      {events.map(event => (
        <EventCard event={event} key={event.eventId} />
      ))}
    </ul>
  )
}

export default EventList;