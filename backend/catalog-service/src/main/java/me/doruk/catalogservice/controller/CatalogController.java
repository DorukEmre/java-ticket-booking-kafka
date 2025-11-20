package me.doruk.catalogservice.controller;

import me.doruk.catalogservice.request.EventCreateRequest;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.EventResponse;
import me.doruk.catalogservice.response.VenueResponse;
import me.doruk.catalogservice.service.EventService;
import me.doruk.catalogservice.service.InventoryService;
import me.doruk.catalogservice.service.VenueService;
import me.doruk.ticketingcommonlibrary.model.ApiErrorResponse;
import me.doruk.ticketingcommonlibrary.model.Cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  // Public

  @Operation(summary = "Get event", description = "Retrieves event information by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponse.class))),
      @ApiResponse(responseCode = "404", description = "Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping("/catalog/events/{eventId}")
  public @ResponseBody EventResponse getEventById(@PathVariable("eventId") Long eventId) {
    System.out.println("GET /api/v1/catalog/events/" + eventId + " called");
    return eventService.getEventInformation(eventId);
  }

  @Operation(summary = "List all events", description = "Lists all available events.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Events retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))))
  })
  @GetMapping("/catalog/events")
  public @ResponseBody List<EventResponse> getAllEvents() {
    System.out.println("GET /api/v1/catalog/events called");
    return eventService.getAllEvents();
  }

  @Operation(summary = "Get venue", description = "Retrieves venue information by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Venue retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class))),
      @ApiResponse(responseCode = "404", description = "Venue not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping("/catalog/venues/{venueId}")
  public @ResponseBody VenueResponse getVenueById(@PathVariable("venueId") Long venueId) {
    System.out.println("GET /api/v1/catalog/venues/" + venueId + " called");
    return venueService.getVenueInformation(venueId);
  }

  @Operation(summary = "List all venues", description = "Lists all available venues.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Venues retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VenueResponse.class))))
  })
  @GetMapping("/catalog/venues")
  public @ResponseBody List<VenueResponse> getAllVenues() {
    System.out.println("GET /api/v1/catalog/venues called");
    return venueService.getAllVenues();
  }

  // Admin

  @Operation(hidden = true, description = "Admin use. Creates a new venue.")
  // @ApiResponses(value = {
  // @ApiResponse(responseCode = "", description = ""),
  // @ApiResponse(responseCode = "", description = "")
  // })
  @PostMapping(value = "/catalog/add-venue", consumes = "application/json", produces = "application/json")
  public ResponseEntity<VenueResponse> createVenue(@RequestBody @Valid VenueCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-venue called");
    VenueResponse createdVenue = venueService.createVenue(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
  }

  @Operation(hidden = true, description = "Admin use. Creates a new event.")
  // @ApiResponses(value = {
  // @ApiResponse(responseCode = "", description = ""),
  // @ApiResponse(responseCode = "", description = "")
  // })
  @PostMapping(value = "/catalog/add-event", consumes = "application/json", produces = "application/json")
  public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventCreateRequest request) {
    System.out.println("POST /api/v1/catalog/add-event called");
    EventResponse createdEvent = eventService.createEvent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
  }

  // Internal - used by other microservices

  @Operation(hidden = true, description = "Internal: used by cart service to validate carts. Hidden from public API docs.")
  @PostMapping("/catalog/validate-cart")
  public ResponseEntity<Map<Long, Boolean>> validateCart(@RequestBody Cart cart) {
    Map<Long, Boolean> result = inventoryService.validateCart(cart);
    return ResponseEntity.ok(result);
  }
}
