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
import me.doruk.ticketingcommonlibrary.event.InventoryReservationFailed;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationSucceeded;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.model.Cart;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CatalogService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  public CatalogService(
      final EventRepository eventRepository,
      final VenueRepository venueRepository,
      final KafkaTemplate<String, Object> kafkaTemplate) {
    this.eventRepository = eventRepository;
    this.venueRepository = venueRepository;
    this.kafkaTemplate = kafkaTemplate;
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
        .build()).toList();
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
        .build()).toList();
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

  // Validate cart from cart-service
  public Map<Long, Boolean> validateCart(final Cart cart) {
    Map<Long, Boolean> result = new HashMap<>();

    // Check if each item is a valid event and has enough capacity
    for (CartItem item : cart.getItems()) {
      final Event event = eventRepository.findById(item.getEventId())
          .orElse(null);

      boolean isValid = event != null
          && item.getTicketCount() != null
          && item.getTicketCount() > 0
          && event.getRemainingCapacity() >= item.getTicketCount();

      log.warn("Item " + item + " is valid: " + isValid
          + ". Event null? " + (event == null)
          + ", remaining capacity: " + (event != null ? event.getRemainingCapacity() : "N/A")
          + ", requested: " + item.getTicketCount());
      // isValid = true; // TEMPORARY FOR TESTING
      result.put(item.getEventId(), isValid);
    }

    return result;
  }

  // Listen for ReserveInventory events from order-service
  @KafkaListener(topics = "reserve-inventory", groupId = "catalog-service")
  public void reserveInventory(ReserveInventory request) {
    System.out.println("Received reserve inventory: " + request);

    List<CartItem> items = request.getItems();
    List<Event> eventsToUpdate = eventRepository.findAllById(
        items.stream().map(CartItem::getEventId).toList());

    // Check all events have enough capacity
    boolean allValid = true;
    for (CartItem item : items) {
      Event event = eventsToUpdate.stream()
          .filter(e -> e.getId().equals(item.getEventId()))
          .findFirst()
          .orElse(null);

      if (event == null || event.getRemainingCapacity() < item.getTicketCount()) {
        allValid = false;
        break;
      }
    }

    // If not all valid, send InventoryReservationFailed and return
    if (!allValid) {
      log.warn("Reservation failed due to insufficient capacity.");

      kafkaTemplate.send("inventory-reservation-failed", InventoryReservationFailed.builder()
          .orderId(request.getOrderId())
          .build());

      return;
    }

    // If all valid, update capacities
    for (CartItem item : items) {
      Event event = eventsToUpdate.stream()
          .filter(e -> e.getId().equals(item.getEventId()))
          .findFirst()
          .orElse(null);

      if (event != null) {
        event.setRemainingCapacity(
            event.getRemainingCapacity() - item.getTicketCount());
      }
    }

    eventRepository.saveAll(eventsToUpdate);
    log.info("Successfully reserved inventory for order " + request.getOrderId());

    // Add ticket price to items
    List<CartItem> updatedItems = items.stream()
        .map(item -> CartItem.builder()
            .eventId(item.getEventId())
            .ticketCount(item.getTicketCount())
            .ticketPrice(eventsToUpdate.stream()
                .filter(e -> e.getId().equals(item.getEventId()))
                .findFirst()
                .map(Event::getTicketPrice)
                .orElse(BigDecimal.ZERO))
            .build())
        .toList();

    // Send InventoryReservationSucceeded event
    kafkaTemplate.send("inventory-reservation-succeeded", InventoryReservationSucceeded.builder()
        .orderId(request.getOrderId())
        .items(updatedItems)
        .build());

  }

}
