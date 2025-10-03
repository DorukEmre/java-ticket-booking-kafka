package me.doruk.catalogservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.catalogservice.entity.Event;
import me.doruk.catalogservice.entity.Venue;
import me.doruk.catalogservice.repository.EventRepository;
import me.doruk.catalogservice.repository.VenueRepository;
import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventCatalogServiceResponse;
import me.doruk.catalogservice.response.VenueCatalogServiceResponse;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationRequested;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CatalogService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;

  @Autowired
  public CatalogService(final EventRepository eventRepository, final VenueRepository venueRepository) {
    this.eventRepository = eventRepository;
    this.venueRepository = venueRepository;
  }

  public EventCatalogServiceResponse getEventInformation(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    return EventCatalogServiceResponse.builder()
        .eventId(event.getId())
        .event(event.getName())
        .capacity(event.getRemainingCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .eventDate(String.valueOf(event.getEventDate()))
        .description(event.getDescription())
        .build();
  }

  public List<EventCatalogServiceResponse> GetAllEvents() {
    final List<Event> events = eventRepository.findAll();

    return events.stream().map(event -> EventCatalogServiceResponse.builder()
        .eventId(event.getId())
        .event(event.getName())
        .capacity(event.getRemainingCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .eventDate(String.valueOf(event.getEventDate()))
        .description(event.getDescription())
        .build()).collect(Collectors.toList());
  }

  public VenueCatalogServiceResponse getVenueInformation(final Long venueId) {
    System.out.println("Fetching venue information for venueId: " + venueId);
    final Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    System.out.println("Found venue: " + venue);
    return VenueCatalogServiceResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .address(venue.getAddress())
        .totalCapacity(venue.getTotalCapacity())
        .build();
  }

  public List<VenueCatalogServiceResponse> getAllVenues() {
    final List<Venue> venues = venueRepository.findAll();

    return venues.stream().map(venue -> VenueCatalogServiceResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .address(venue.getAddress())
        .totalCapacity(venue.getTotalCapacity())
        .build()).collect(Collectors.toList());
  }

  public VenueCatalogServiceResponse createVenue(final VenueCreateRequest request) {
    System.out.println("Creating venue: " + request);
    Venue venue = new Venue();
    venue.setName(request.getName());
    venue.setAddress(request.getAddress());
    venue.setTotalCapacity(request.getTotalCapacity());

    Venue savedVenue = venueRepository.save(venue);

    return VenueCatalogServiceResponse.builder()
        .venueId(savedVenue.getId())
        .name(savedVenue.getName())
        .address(savedVenue.getAddress())
        .totalCapacity(savedVenue.getTotalCapacity())
        .build();
  }

  public EventCatalogServiceResponse createEvent(final EventCreateRequest request) {
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
        .build();

    Event savedEvent = eventRepository.save(event);

    return EventCatalogServiceResponse.builder()
        .eventId(savedEvent.getId())
        .event(savedEvent.getName())
        .capacity(savedEvent.getRemainingCapacity())
        .venue(savedEvent.getVenue())
        .ticketPrice(savedEvent.getTicketPrice())
        .eventDate(String.valueOf(savedEvent.getEventDate()))
        .description(savedEvent.getDescription())
        .build();
  }

  private void updateEventCapacity(final Long eventId, final Long ticketBooked) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    event.setRemainingCapacity(event.getRemainingCapacity() - ticketBooked);

    eventRepository.save(event);

    log.info("Updated event capacity for event {} with tickets booked {}", eventId, ticketBooked);
  }

  public void updateEventsCapacities(final List<InventoryReservationRequested> eventTicketCounts) {
    System.out.println("Updating event capacities: " + eventTicketCounts);
    for (var entry : eventTicketCounts) {
      updateEventCapacity(entry.getEventId(), entry.getTicketCount());
    }
    log.info("Updated capacities for all events");
  }

}
