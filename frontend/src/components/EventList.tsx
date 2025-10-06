import type { Event } from '@/types/catalog';

type EventListProps = {
  events: Event[];
};

function EventList({ events }: EventListProps) {
  return (
    <ul>
      {events.map(event => (
        <li key={event.eventId}>
          Event ID {event.venueId}: {event.name} on {new Date(event.date).toLocaleDateString()}
        </li>
      ))}
    </ul>
  )

}

export default EventList;