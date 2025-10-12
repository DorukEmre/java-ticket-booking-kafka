import EventCard from '@/components/EventCard';

import type { Event } from '@/types/catalog';

function EventList({ events }: { events: Event[] }) {

  // Sort events by next occurrence date/month (ignore year) and take first 6
  const displayedEvents = [...events].sort((a: Event, b: Event) => {
    const toNextOccurrenceTime = (event: Event) => {
      const parsed = Date.parse(String(event.eventDate));
      if (Number.isNaN(parsed)) return Infinity; // unknown dates go to the end

      const eventDate = new Date(parsed);
      const now = new Date();
      // Create date with current year and month + date of event
      const candidate = new Date(now.getFullYear(), eventDate.getMonth(), eventDate.getDate());
      // Today's date
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

      // If candidate already passed this year, use next year
      if (candidate.getTime() < today.getTime()) {
        candidate.setFullYear(candidate.getFullYear() + 1);
      }

      return candidate.getTime();
    };

    return toNextOccurrenceTime(a) - toNextOccurrenceTime(b);
  }).slice(0, 6);

  return (
    <ul className="d-flex list-unstyled flex-wrap justify-content-center justify-content-md-between align-items-stretch gap-4">
      {displayedEvents.map(event => (
        <EventCard event={event} key={event.eventId} />
      ))}
    </ul>
  )
}

export default EventList;