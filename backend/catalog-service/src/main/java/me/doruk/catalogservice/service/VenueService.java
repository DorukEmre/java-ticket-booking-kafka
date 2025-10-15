package me.doruk.catalogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.catalogservice.entity.Venue;
import me.doruk.catalogservice.repository.VenueRepository;
import me.doruk.catalogservice.request.VenueCreateRequest;
import me.doruk.catalogservice.response.VenueResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VenueService {

  private final VenueRepository venueRepository;

  public VenueResponse getVenueInformation(final Long venueId) {
    System.out.println("Fetching venue information for venueId: " + venueId);
    final Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));

    return VenueResponse.builder()
        .id(venue.getId())
        .name(venue.getName())
        .location(venue.getLocation())
        .totalCapacity(venue.getTotalCapacity())
        .imageUrl(venue.getImageUrl())
        .build();
  }

  public List<VenueResponse> getAllVenues() {
    final List<Venue> venues = venueRepository.findAll();

    return venues.stream().map(venue -> VenueResponse.builder()
        .id(venue.getId())
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
        .id(savedVenue.getId())
        .name(savedVenue.getName())
        .location(savedVenue.getLocation())
        .totalCapacity(savedVenue.getTotalCapacity())
        .imageUrl(savedVenue.getImageUrl())
        .build();
  }

}
