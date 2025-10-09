package me.doruk.catalogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.catalogservice.entity.Event;
import me.doruk.catalogservice.entity.Venue;
import me.doruk.catalogservice.repository.EventRepository;
import me.doruk.catalogservice.repository.VenueRepository;
import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventResponse;
import me.doruk.catalogservice.response.VenueResponse;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationFailed;
import me.doruk.ticketingcommonlibrary.event.InventoryReservationSucceeded;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.model.Cart;
import me.doruk.ticketingcommonlibrary.model.CartItem;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogService {

  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public EventResponse getEventInformation(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

    return EventResponse.builder()
        .eventId(event.getId())
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
        .eventId(event.getId())
        .name(event.getName())
        .capacity(event.getRemainingCapacity())
        .venue(event.getVenue())
        .ticketPrice(event.getTicketPrice())
        .eventDate(String.valueOf(event.getEventDate()))
        .description(event.getDescription())
        .imageUrl(event.getImageUrl())
        .build()).toList();
  }

  public VenueResponse getVenueInformation(final Long venueId) {
    System.out.println("Fetching venue information for venueId: " + venueId);
    final Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));

    return VenueResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .location(venue.getLocation())
        .totalCapacity(venue.getTotalCapacity())
        .imageUrl(venue.getImageUrl())
        .build();
  }

  public List<VenueResponse> getAllVenues() {
    final List<Venue> venues = venueRepository.findAll();

    return venues.stream().map(venue -> VenueResponse.builder()
        .venueId(venue.getId())
        .name(venue.getName())
        .location(venue.getLocation())
        .totalCapacity(venue.getTotalCapacity())
        .imageUrl(venue.getImageUrl())
        .build()).toList();
  }

  public VenueResponse createVenue(final VenueCreateRequest request) {
    System.out.println("Creating venue: " + request);
    Venue venue = new Venue();
    venue.setName(request.getName());
    venue.setLocation(request.getLocation());
    venue.setTotalCapacity(request.getTotalCapacity());

    Venue savedVenue = venueRepository.save(venue);

    return VenueResponse.builder()
        .venueId(savedVenue.getId())
        .name(savedVenue.getName())
        .location(savedVenue.getLocation())
        .totalCapacity(savedVenue.getTotalCapacity())
        .imageUrl(savedVenue.getImageUrl())
        .build();
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
        .eventId(savedEvent.getId())
        .name(savedEvent.getName())
        .capacity(savedEvent.getRemainingCapacity())
        .venue(savedEvent.getVenue())
        .ticketPrice(savedEvent.getTicketPrice())
        .eventDate(String.valueOf(savedEvent.getEventDate()))
        .description(savedEvent.getDescription())
        .imageUrl(savedEvent.getImageUrl())
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
  @Transactional
  @KafkaListener(topics = "reserve-inventory", groupId = "catalog-service")
  public void reserveInventory(ReserveInventory request) {
    System.out.println("Received reserve inventory: " + request);

    List<CartItem> items = request.getItems();
    List<Long> eventIds = items.stream().map(CartItem::getEventId).toList();
    List<Event> eventsToUpdate = eventRepository.findAllByIdForUpdate(eventIds);

    List<CartItem> updatedItems = new ArrayList<CartItem>();

    // Validate items' price and capacity
    for (CartItem item : items) {
      Event event = eventsToUpdate.stream()
          .filter(e -> e.getId().equals(item.getEventId()))
          .findFirst()
          .orElse(null);

      // Check price change
      BigDecimal previousPrice = item.getTicketPrice() == null ? BigDecimal.ZERO : item.getTicketPrice();
      BigDecimal currentPrice = (event != null && event.getTicketPrice() != null) ? event.getTicketPrice()
          : BigDecimal.ZERO;
      boolean priceChanged = previousPrice.compareTo(currentPrice) != 0;

      // Check capacity
      boolean available = event != null && event.getRemainingCapacity() >= item.getTicketCount();

      CartItem updated = CartItem.builder()
          .eventId(item.getEventId())
          .ticketCount(item.getTicketCount())
          .previousPrice(previousPrice)
          .ticketPrice(currentPrice)
          .priceChanged(priceChanged)
          .available(available)
          .build();

      updatedItems.add(updated);
    }

    // Valid is available and NO priceChanged
    boolean allValid = updatedItems.stream().allMatch(i -> i.isAvailable() && !i.isPriceChanged());

    // If not all valid, send InventoryReservationFailed and return
    if (!allValid) {
      log.warn("Reservation failed: {}", updatedItems);

      kafkaTemplate.send("inventory-reservation-failed", InventoryReservationFailed.builder()
          .orderId(request.getOrderId())
          .items(updatedItems)
          .build());

      return;
    }

    // If all valid, update remaining event capacities
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

    // Save updated events to db
    eventRepository.saveAll(eventsToUpdate);
    log.info("Successfully reserved inventory for order " + request.getOrderId());

    // Send InventoryReservationSucceeded event
    kafkaTemplate.send("inventory-reservation-succeeded", InventoryReservationSucceeded.builder()
        .orderId(request.getOrderId())
        .items(updatedItems)
        .build());

  }

}
