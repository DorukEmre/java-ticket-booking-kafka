package me.doruk.cartservice.service;

import lombok.extern.slf4j.Slf4j;
import me.doruk.cartservice.client.CatalogServiceClient;
import me.doruk.cartservice.event.CartEvent;
import me.doruk.cartservice.event.CartEventItem;
import me.doruk.cartservice.request.CartRequest;
import me.doruk.cartservice.request.CartRequestItem;
import me.doruk.cartservice.response.CartResponse;
import me.doruk.cartservice.response.CatalogServiceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class CartService {

  private final CatalogServiceClient catalogServiceClient;
  private final KafkaTemplate<String, CartEvent> kafkaTemplate;

  @Autowired
  public CartService(
      final CatalogServiceClient catalogServiceClient,
      final KafkaTemplate<String, CartEvent> kafkaTemplate) {
    this.catalogServiceClient = catalogServiceClient;
    this.kafkaTemplate = kafkaTemplate;
  }

  public CartResponse createCart(final CartRequest request) {
    System.out.println("Create cart called: " + request);

    List<CartRequestItem> items = request.getItems();
    if (items == null || items.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart request must contain at least one item");
    }
    for (CartRequestItem item : items) {
      // check if enough catalog
      // --- get event information to also get Venue information
      final CatalogServiceResponse catalogResponse = catalogServiceClient.getCatalogService(item.getEventId());
      System.out.println(catalogResponse);

      if (catalogResponse.getCapacity() < item.getTicketCount())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough tickets available");
    }

    // create cart
    final CartEvent cartEvent = createCartEvent(request, items);
    System.out.println(cartEvent);

    // send cart to Order Service on a Kafka Topic
    kafkaTemplate.send("order-requested", cartEvent)
        .thenAccept(result -> log.info("Cart event sent successfully: {}", cartEvent))
        .exceptionally(ex -> {
          log.error("Failed to send order-requested event: {}", cartEvent, ex);
          return null;
        });

    return CartResponse.builder()
        .customerName(request.getCustomerName())
        .numberOfItems(items.size())
        .build();
  }

  private CartEvent createCartEvent(final CartRequest request,
      final List<CartRequestItem> items) {

    return CartEvent.builder()
        .id(request.getId())
        .customerName(request.getCustomerName())
        .email(request.getEmail())
        .items(items.stream()
            .map((CartRequestItem item) -> CartEventItem.builder()
                .eventId(item.getEventId())
                .ticketCount(item.getTicketCount())
                .ticketPrice(item.getTicketPrice())
                .build())
            .toList())
        .build();
  }
}
