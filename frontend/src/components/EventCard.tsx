import { Link } from "react-router-dom";

import type { Event } from "@/types/catalog";
import { imageBaseUrl } from "@/utils/globals";

function EventCard({ event }: { event: Event }) {

  const bgImage = {
    ...(event.imageUrl
      ? { backgroundImage: `url(${imageBaseUrl}${event.imageUrl})` }
      : { backgroundColor: '#333' }),
  };

  // Split date
  const date = new Date(event.eventDate);
  const dayOfMonth = date.getDate();
  const monthShort = date.toLocaleString(undefined, { month: 'short' });

  return (
    <li style={bgImage} className="card__image">
      <Link to={`/events/${event.eventId}`}
        className="position-relative d-flex gap-3 h-100 w-100"
      >
        <div className="card__overlay" aria-hidden="true" />
        <div data-role="date" className="position-absolute top-0 text-white p-2 bg-compl-300 bg-opacity-75 d-flex flex-column align-items-center" style={{ left: '1.5rem', minWidth: '3.25rem' }}>
          <span className="date__day fw-bold" aria-hidden="true">{dayOfMonth}</span>
          <span className="date__month" aria-hidden="true">{monthShort}</span>
          <span className="visually-hidden">{date.toLocaleDateString()}</span>
        </div>
        <div data-role="price" className="position-absolute top-0 text-white p-2 fw-bold bg-compl-300 bg-opacity-75" style={{ right: '1.5rem', fontSize: '0.9rem' }}>
          {event.ticketPrice.toFixed(2)}{'\u00A0€'}
        </div>
        <div data-role="detail" className="position-absolute bottom-0 start-0 text-white p-4 w-100">
          <p className="fw-bold">{event.name}</p>
          <p>{event.venue.location}</p>
        </div>
      </Link>
    </li >
  )
}

export default EventCard;