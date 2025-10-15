package me.doruk.catalogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.catalogservice.entity.Event;
import me.doruk.catalogservice.entity.Venue;
import me.doruk.catalogservice.repository.EventRepository;
import me.doruk.catalogservice.repository.VenueRepository;
import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.response.EventResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;

  public EventResponse getEventInformation(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    return EventResponse.builder()
        .id(event.getId())
        .name(event.getName())
        .capacity(event.getRemainingCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .eventDate(String.valueOf(event.getEventDate()))
        .description(event.getDescription())
        .imageUrl(event.getImageUrl())
        .build();
  }

  public List<EventResponse> GetAllEvents() {
    final List<Event> events = eventRepository.findAll();

    return events.stream().map(event -> EventResponse.builder()
        .id(event.getId())
        .name(event.getName())
        .capacity(event.getRemainingCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .eventDate(String.valueOf(event.getEventDate()))
        .description(event.getDescription())
        .imageUrl(event.getImageUrl())
        .build()).toList();
  }

  public EventResponse createEvent(final EventCreateRequest request) {
    System.out.println("Creating event: " + request);

    final Venue venue = venueRepository.findById(request.getVenueId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Venue with ID " + request.getVenueId() + " does not exist."));

    if (request.getTotalCapacity() > venue.getTotalCapacity()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Event capacity cannot exceed venue capacity.");
    }

    Event event = Event.builder()
        .name(request.getName())
        .totalCapacity(request.getTotalCapacity())
        .remainingCapacity(request.getTotalCapacity())
        .venue(venue)
        .ticketPrice(request.getTicketPrice())
        .eventDate(Date.valueOf(request.getEventDate()))
        .description(request.getDescription())
        .imageUrl(request.getImageUrl())
        .build();

    Event savedEvent = eventRepository.save(event);

    return EventResponse.builder()
        .id(savedEvent.getId())
        .name(savedEvent.getName())
        .capacity(savedEvent.getRemainingCapacity())
        .venue(savedEvent.getVenue())
        .ticketPrice(savedEvent.getTicketPrice())
        .eventDate(String.valueOf(savedEvent.getEventDate()))
        .description(savedEvent.getDescription())
        .imageUrl(savedEvent.getImageUrl())
        .build();
  }

}
