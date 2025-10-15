package me.doruk.catalogservice.controller;

import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventResponse;
import me.doruk.catalogservice.response.VenueResponse;
import me.doruk.catalogservice.service.EventService;
import me.doruk.catalogservice.service.InventoryService;
import me.doruk.catalogservice.service.VenueService;
import me.doruk.ticketingcommonlibrary.model.Cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CatalogController {

  private final InventoryService inventoryService;
  private final EventService eventService;
  private final VenueService venueService;

  @GetMapping("/catalog/events/{eventId}")
  public @ResponseBody EventResponse catalogByEventId(@PathVariable("eventId") Long eventId) {
    System.out.println("GET /api/v1/catalog/events/" + eventId + " called");
    return eventService.getEventInformation(eventId);
  }

  @GetMapping("/catalog/events")
  public @ResponseBody List<EventResponse> catalogGetAllEvents() {
    System.out.println("GET /api/v1/catalog/events called");
    return eventService.GetAllEvents();
  }

  @GetMapping("/catalog/venues/{venueId}")
  public @ResponseBody VenueResponse catalogByVenueId(@PathVariable("venueId") Long venueId) {
    System.out.println("GET /api/v1/catalog/venues/" + venueId + " called");
    return venueService.getVenueInformation(venueId);
  }

  @GetMapping("/catalog/venues")
  public @ResponseBody List<VenueResponse> catalogGetAllVenues() {
    System.out.println("GET /api/v1/catalog/venues called");
    return venueService.getAllVenues();
  }

  @PostMapping(value = "/catalog/add-venue", consumes = "application/json", produces = "application/json")
  public ResponseEntity<VenueResponse> createVenue(@RequestBody @Valid VenueCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-venue called");
    VenueResponse createdVenue = venueService.createVenue(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
  }

  @PostMapping(value = "/catalog/add-event", consumes = "application/json", produces = "application/json")
  public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-event called");
    EventResponse createdEvent = eventService.createEvent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
  }

  @PostMapping("/catalog/validate-cart")
  public ResponseEntity<Map<Long, Boolean>> validateCart(@RequestBody Cart cart) {
    Map<Long, Boolean> result = inventoryService.validateCart(cart);
    return ResponseEntity.ok(result);
  }
}
