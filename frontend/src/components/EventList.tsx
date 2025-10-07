import type { Event } from '@/types/catalog';
import { Link } from 'react-router-dom';

type EventListProps = {
  events: Event[];
};

function EventList({ events }: EventListProps) {

  return (
    <ul>
      {events.map(event => (
        <li key={event.eventId}>
          <Link to={`/events/${event.eventId}`}>
            Event ID {event.eventId}: {event.name} on {new Date(event.eventDate).toLocaleDateString()}
          </Link>
        </li>
      ))}
    </ul>
  )
}

export default EventList;