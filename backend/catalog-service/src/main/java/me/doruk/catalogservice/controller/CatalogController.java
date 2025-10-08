package me.doruk.catalogservice.controller;

import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventResponse;
import me.doruk.catalogservice.response.VenueResponse;
import me.doruk.catalogservice.service.CatalogService;
import me.doruk.ticketingcommonlibrary.model.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("api/v1")
public class CatalogController {

  private final CatalogService catalogService;

  @Autowired
  public CatalogController(final CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/catalog/events/{eventId}")
  public @ResponseBody EventResponse catalogByEventId(@PathVariable("eventId") Long eventId) {
    System.out.println("GET /api/v1/catalog/events/" + eventId + " called");
    return catalogService.getEventInformation(eventId);
  }

  @GetMapping("/catalog/events")
  public @ResponseBody List<EventResponse> catalogGetAllEvents() {
    System.out.println("GET /api/v1/catalog/events called");
    return catalogService.GetAllEvents();
  }

  @GetMapping("/catalog/venues/{venueId}")
  public @ResponseBody VenueResponse catalogByVenueId(@PathVariable("venueId") Long venueId) {
    System.out.println("GET /api/v1/catalog/venues/" + venueId + " called");
    return catalogService.getVenueInformation(venueId);
  }

  @GetMapping("/catalog/venues")
  public @ResponseBody List<VenueResponse> catalogGetAllVenues() {
    System.out.println("GET /api/v1/catalog/venues called");
    return catalogService.getAllVenues();
  }

  @PostMapping(value = "/catalog/add-venue", consumes = "application/json", produces = "application/json")
  public ResponseEntity<VenueResponse> createVenue(@RequestBody @Valid VenueCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-venue called");
    VenueResponse createdVenue = catalogService.createVenue(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
  }

  @PostMapping(value = "/catalog/add-event", consumes = "application/json", produces = "application/json")
  public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-event called");
    EventResponse createdEvent = catalogService.createEvent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
  }

  @PostMapping("/catalog/validate-cart")
  public ResponseEntity<Map<Long, Boolean>> validateCart(@RequestBody Cart cart) {
    Map<Long, Boolean> result = catalogService.validateCart(cart);
    return ResponseEntity.ok(result);
  }
}
