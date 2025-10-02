package me.doruk.inventory.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.inventory.entity.Event;
import me.doruk.inventory.entity.Venue;
import me.doruk.inventory.repository.EventRepository;
import me.doruk.inventory.repository.VenueRepository;
import me.doruk.inventory.request.EventCreateRequest;
import me.doruk.inventory.request.VenueCreateRequest;
import me.doruk.inventory.response.EventInventoryResponse;
import me.doruk.inventory.response.VenueInventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;

  @Autowired
  public InventoryService(final EventRepository eventRepository, final VenueRepository venueRepository) {
    this.eventRepository = eventRepository;
    this.venueRepository = venueRepository;
  }

  public EventInventoryResponse getEventInformation(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    return EventInventoryResponse.builder()
        .eventId(event.getId())
        .event(event.getName())
        .capacity(event.getLeftCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .build();
  }

  public List<EventInventoryResponse> GetAllEvents() {
    final List<Event> events = eventRepository.findAll();

    return events.stream().map(event -> EventInventoryResponse.builder()
        .eventId(event.getId())
        .event(event.getName())
        .capacity(event.getLeftCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .build()).collect(Collectors.toList());
  }

  public VenueInventoryResponse getVenueInformation(final Long venueId) {
    System.out.println("Fetching venue information for venueId: " + venueId);
    final Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    System.out.println("Found venue: " + venue);
    return VenueInventoryResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .address(venue.getAddress())
        .totalCapacity(venue.getTotalCapacity())
        .build();
  }

  public List<VenueInventoryResponse> getAllVenues() {
    final List<Venue> venues = venueRepository.findAll();

    return venues.stream().map(venue -> VenueInventoryResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .address(venue.getAddress())
        .totalCapacity(venue.getTotalCapacity())
        .build()).collect(Collectors.toList());
  }

  public VenueInventoryResponse createVenue(final VenueCreateRequest request) {
    System.out.println("Creating venue: " + request);
    Venue venue = new Venue();
    venue.setName(request.getName());
    venue.setAddress(request.getAddress());
    venue.setTotalCapacity(request.getTotalCapacity());

    Venue savedVenue = venueRepository.save(venue);

    return VenueInventoryResponse.builder()
        .venueId(savedVenue.getId())
        .name(savedVenue.getName())
        .address(savedVenue.getAddress())
        .totalCapacity(savedVenue.getTotalCapacity())
        .build();
  }

  public EventInventoryResponse createEvent(final EventCreateRequest request) {
    System.out.println("Creating event: " + request);

    final Venue venue = venueRepository.findById(request.getVenueId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Venue with ID " + request.getVenueId() + " does not exist."));

    Event event = new Event();
    event.setName(request.getName());
    event.setTotalCapacity(request.getTotalCapacity());
    event.setLeftCapacity(request.getTotalCapacity());
    event.setVenue(venue);
    event.setTicketPrice(request.getTicketPrice());

    if (request.getTotalCapacity() > venue.getTotalCapacity()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Event capacity cannot exceed venue capacity.");
    }

    Event savedEvent = eventRepository.save(event);

    return EventInventoryResponse.builder()
        .eventId(savedEvent.getId())
        .event(savedEvent.getName())
        .capacity(savedEvent.getLeftCapacity())
        .venue(savedEvent.getVenue())
        .ticketPrice(savedEvent.getTicketPrice())
        .build();
  }

  public void updateEventCapacity(final Long eventId, final Long ticketBooked) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    event.setLeftCapacity(event.getLeftCapacity() - ticketBooked);

    eventRepository.save(event);

    log.info("Updated event capacity for event {} with tickets booked {}", eventId, ticketBooked);
  }

}
