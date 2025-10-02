package me.doruk.inventory.controller;

import me.doruk.inventory.request.EventCreateRequest;
import me.doruk.inventory.request.VenueCreateRequest;
import me.doruk.inventory.response.EventInventoryResponse;
import me.doruk.inventory.response.VenueInventoryResponse;
import me.doruk.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class InventoryController {

  private final InventoryService inventoryService;

  @Autowired
  public InventoryController(final InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping("/inventory/event/{eventId}")
  public @ResponseBody EventInventoryResponse inventoryByEventId(@PathVariable("eventId") Long eventId) {
    System.out.println("GET /api/v1/inventory/event/" + eventId + " called");
    return inventoryService.getEventInformation(eventId);
  }

  @GetMapping("/inventory/events")
  public @ResponseBody List<EventInventoryResponse> inventoryGetAllEvents() {
    System.out.println("GET /api/v1/inventory/events called");
    return inventoryService.GetAllEvents();
  }

  @GetMapping("/inventory/venue/{venueId}")
  public @ResponseBody VenueInventoryResponse inventoryByVenueId(@PathVariable("venueId") Long venueId) {
    System.out.println("GET /api/v1/inventory/venue/" + venueId + " called");
    return inventoryService.getVenueInformation(venueId);
  }

  @GetMapping("/inventory/venues")
  public @ResponseBody List<VenueInventoryResponse> inventoryGetAllVenues() {
    System.out.println("GET /api/v1/inventory/venues called");
    return inventoryService.getAllVenues();
  }

  @PostMapping(value = "/inventory/add-venue", consumes = "application/json", produces = "application/json")
  public ResponseEntity<VenueInventoryResponse> createVenue(@RequestBody VenueCreateRequest request) {
    System.out.println("POST /api/v1/inventory/add-venue called");
    VenueInventoryResponse createdVenue = inventoryService.createVenue(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
  }

  @PostMapping(value = "/inventory/add-event", consumes = "application/json", produces = "application/json")
  public ResponseEntity<EventInventoryResponse> createEvent(@RequestBody EventCreateRequest request) {
    System.out.println("POST /api/v1/inventory/add-event called");
    EventInventoryResponse createdEvent = inventoryService.createEvent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
  }

  @PutMapping("/events/update-capacities")
  public ResponseEntity<Void> updateCapacities(@RequestBody List<SimpleEntry<Long, Long>> eventTicketCounts) {
    inventoryService.updateEventsCapacities(eventTicketCounts);
    return ResponseEntity.ok().build();
  }
}
