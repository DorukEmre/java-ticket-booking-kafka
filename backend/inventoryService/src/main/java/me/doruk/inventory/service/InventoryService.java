package me.doruk.inventory.service;

import me.doruk.inventory.entity.Event;
import me.doruk.inventory.entity.Venue;
import me.doruk.inventory.repository.EventRepository;
import me.doruk.inventory.repository.VenueRepository;
import me.doruk.inventory.request.EventCreateRequest;
import me.doruk.inventory.request.VenueCreateRequest;
import me.doruk.inventory.response.EventInventoryResponse;
import me.doruk.inventory.response.VenueInventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;

  @Autowired
  public InventoryService(final EventRepository eventRepository, final VenueRepository venueRepository) {
    this.eventRepository = eventRepository;
    this.venueRepository = venueRepository;
  }

  public EventInventoryResponse getEventInformation(final Long eventId) {
    final Event event = eventRepository.findById(eventId).orElse(null);

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
    final Venue venue = venueRepository.findById(venueId).orElse(null);

    return VenueInventoryResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .address(venue.getAddress())
        .totalCapacity(venue.getTotalCapacity())
        .build();
  }

  public List<VenueInventoryResponse> GetAllVenues() {
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

    final Venue venue = venueRepository.findById(request.getVenueId()).orElse(null);
    if (venue == null) {
      throw new IllegalArgumentException("Venue with ID " + request.getVenueId() + " does not exist.");
    }
    Event event = new Event();
    event.setName(request.getName());
    event.setTotalCapacity(request.getTotalCapacity());
    event.setLeftCapacity(request.getTotalCapacity());
    event.setVenue(venue);
    event.setTicketPrice(request.getTicketPrice());

    if (request.getTotalCapacity() > venue.getTotalCapacity()) {
      throw new IllegalArgumentException("Event capacity cannot exceed venue capacity.");
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
}
