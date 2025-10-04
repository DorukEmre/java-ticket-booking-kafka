package me.doruk.catalogservice.controller;

import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventCatalogServiceResponse;
import me.doruk.catalogservice.response.VenueCatalogServiceResponse;
import me.doruk.catalogservice.service.CatalogService;
import me.doruk.ticketingcommonlibrary.event.ReserveInventory;
import me.doruk.ticketingcommonlibrary.model.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class CatalogServiceController {

  private final CatalogService catalogService;

  @Autowired
  public CatalogServiceController(final CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/catalog/event/{eventId}")
  public @ResponseBody EventCatalogServiceResponse catalogByEventId(@PathVariable("eventId") Long eventId) {
    System.out.println("GET /api/v1/catalog/event/" + eventId + " called");
    return catalogService.getEventInformation(eventId);
  }

  @GetMapping("/catalog/events")
  public @ResponseBody List<EventCatalogServiceResponse> catalogGetAllEvents() {
    System.out.println("GET /api/v1/catalog/events called");
    return catalogService.GetAllEvents();
  }

  @GetMapping("/catalog/venue/{venueId}")
  public @ResponseBody VenueCatalogServiceResponse catalogByVenueId(@PathVariable("venueId") Long venueId) {
    System.out.println("GET /api/v1/catalog/venue/" + venueId + " called");
    return catalogService.getVenueInformation(venueId);
  }

  @GetMapping("/catalog/venues")
  public @ResponseBody List<VenueCatalogServiceResponse> catalogGetAllVenues() {
    System.out.println("GET /api/v1/catalog/venues called");
    return catalogService.getAllVenues();
  }

  @PostMapping(value = "/catalog/add-venue", consumes = "application/json", produces = "application/json")
  public ResponseEntity<VenueCatalogServiceResponse> createVenue(@RequestBody VenueCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-venue called");
    VenueCatalogServiceResponse createdVenue = catalogService.createVenue(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
  }

  @PostMapping(value = "/catalog/add-event", consumes = "application/json", produces = "application/json")
  public ResponseEntity<EventCatalogServiceResponse> createEvent(@RequestBody EventCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-event called");
    EventCatalogServiceResponse createdEvent = catalogService.createEvent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
  }

  @PutMapping("/catalog/events/update-capacities")
  public ResponseEntity<Void> updateCapacities(@RequestBody List<ReserveInventory> eventTicketCounts) {

    // if (eventTicketCounts.size() > 0)
    // throw new IllegalArgumentException("Test exception for debugging");

    catalogService.updateEventsCapacities(eventTicketCounts);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/catalog/validate-cart")
  public ResponseEntity<Map<Long, Boolean>> validateCart(@RequestBody Cart cart) {
    Map<Long, Boolean> result = catalogService.validateCart(cart);
    return ResponseEntity.ok(result);
  }
}
