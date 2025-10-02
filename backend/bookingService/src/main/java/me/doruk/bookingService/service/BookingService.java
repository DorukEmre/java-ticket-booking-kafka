package me.doruk.bookingService.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.bookingService.client.CatalogServiceClient;
import me.doruk.bookingService.event.BookingEvent;
import me.doruk.bookingService.event.BookingEventItem;
import me.doruk.bookingService.request.BookingRequest;
import me.doruk.bookingService.request.BookingRequestItem;
import me.doruk.bookingService.response.BookingResponse;
import me.doruk.bookingService.response.CatalogServiceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class BookingService {

  private final CatalogServiceClient catalogServiceClient;
  private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

  @Autowired
  public BookingService(
      final CatalogServiceClient catalogServiceClient,
      final KafkaTemplate<String, BookingEvent> kafkaTemplate) {
    this.catalogServiceClient = catalogServiceClient;
    this.kafkaTemplate = kafkaTemplate;
  }

  public BookingResponse createBooking(final BookingRequest request) {
    System.out.println("Create booking called: " + request);

    List<BookingRequestItem> items = request.getItems();
    if (items == null || items.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking request must contain at least one item");
    }
    for (BookingRequestItem item : items) {
      // check if enough catalog
      // --- get event information to also get Venue information
      final CatalogServiceResponse catalogResponse = catalogServiceClient.getCatalogService(item.getEventId());
      System.out.println(catalogResponse);

      if (catalogResponse.getCapacity() < item.getTicketCount())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough tickets available");
    }

    // create booking
    final BookingEvent bookingEvent = createBookingEvent(request, items);
    System.out.println(bookingEvent);

    // send booking to Order Service on a Kafka Topic
    kafkaTemplate.send("booking", bookingEvent)
        .thenAccept(result -> log.info("Booking event sent successfully: {}", bookingEvent))
        .exceptionally(ex -> {
          log.error("Failed to send booking event: {}", bookingEvent, ex);
          return null;
        });

    return BookingResponse.builder()
        .customerName(request.getCustomerName())
        .numberOfItems(items.size())
        .build();
  }

  private BookingEvent createBookingEvent(final BookingRequest request,
      final List<BookingRequestItem> items) {

    return BookingEvent.builder()
        .id(request.getId())
        .customerName(request.getCustomerName())
        .email(request.getEmail())
        .bookingEventItems(items.stream()
            .map((BookingRequestItem item) -> BookingEventItem.builder()
                .eventId(item.getEventId())
                .ticketCount(item.getTicketCount())
                .ticketPrice(item.getTicketPrice())
                .build())
            .toList())
        .build();
  }
}
