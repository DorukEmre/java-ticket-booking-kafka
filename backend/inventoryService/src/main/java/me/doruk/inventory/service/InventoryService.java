package me.doruk.inventory.service;

import me.doruk.inventory.entity.Event;
import me.doruk.inventory.entity.Venue;
import me.doruk.inventory.repository.EventRepository;
import me.doruk.inventory.repository.VenueRepository;
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

  public List<EventInventoryResponse> GetAllEvents() {
    final List<Event> events = eventRepository.findAll();

    return events.stream().map(event -> EventInventoryResponse.builder()
        .event(event.getName())
        .capacity(event.getLeftCapacity())
        .venue(event.getVenue())
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

  public VenueInventoryResponse createVenue(final VenueInventoryResponse venueRequest) {
    System.out.println("Creating venue: " + venueRequest);
    Venue venue = new Venue();
    venue.setName(venueRequest.getName());
    venue.setAddress(venueRequest.getAddress());
    venue.setTotalCapacity(venueRequest.getTotalCapacity());

    Venue savedVenue = venueRepository.save(venue);

    return VenueInventoryResponse.builder()
        .venueId(savedVenue.getId())
        .name(savedVenue.getName())
        .address(savedVenue.getAddress())
        .totalCapacity(savedVenue.getTotalCapacity())
        .build();
  }
}
