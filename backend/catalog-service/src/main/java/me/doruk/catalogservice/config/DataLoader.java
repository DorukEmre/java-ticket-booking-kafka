package me.doruk.catalogservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.doruk.catalogservice.entity.Event;
import me.doruk.catalogservice.entity.Venue;
import me.doruk.catalogservice.repository.EventRepository;
import me.doruk.catalogservice.repository.VenueRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader {
  private final EventRepository eventRepository;
  private final VenueRepository venueRepository;

  // Helper classes for JSON mapping
  @Data
  static class VenueJson {
    private String name;
    private String location;
    private int totalCapacity;
    private String imageUrl;
  }

  @Data
  static class EventJson {
    private String name;
    private int totalCapacity;
    private int venueIndex;
    private double ticketPrice;
    private String description;
    private String eventDate;
    private String imageUrl;
  }

  // Load initial events and venues data from JSON files into the database
  @PostConstruct
  public void loadData() {
    ObjectMapper mapper = new ObjectMapper();

    try {

      if (venueRepository.count() == 0) {

        List<VenueJson> venueJsons = mapper.readValue(
            new ClassPathResource("venues.json").getInputStream(),
            new TypeReference<List<VenueJson>>() {
            });

        List<Venue> venues = venueJsons.stream().map(vj -> Venue.builder()
            .name(vj.getName())
            .location(vj.getLocation())
            .totalCapacity(vj.getTotalCapacity())
            .imageUrl(vj.getImageUrl())
            .build()).toList();

        venueRepository.saveAllAndFlush(venues);
      }

      if (venueRepository.count() != 0 && eventRepository.count() == 0) {

        List<Venue> venues = venueRepository.findAll();

        List<EventJson> eventJsons = mapper.readValue(
            new ClassPathResource("events.json").getInputStream(),
            new TypeReference<List<EventJson>>() {
            });

        List<Event> events = eventJsons.stream().map(ej -> Event.builder()
            .name(ej.getName())
            .totalCapacity(ej.getTotalCapacity())
            .venue(venues.get(ej.getVenueIndex()))
            .ticketPrice(BigDecimal.valueOf(ej.getTicketPrice()))
            .description(ej.getDescription())
            .eventDate(Date.valueOf(ej.getEventDate()))
            .imageUrl(ej.getImageUrl())
            .build()).toList();

        eventRepository.saveAll(events);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
