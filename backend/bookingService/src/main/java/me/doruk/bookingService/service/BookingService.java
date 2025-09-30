package me.doruk.bookingService.service;

import me.doruk.bookingService.client.InventoryServiceClient;
import me.doruk.bookingService.entity.Customer;
import me.doruk.bookingService.repository.CustomerRepository;
import me.doruk.bookingService.request.BookingRequest;
import me.doruk.bookingService.response.BookingResponse;
import me.doruk.bookingService.response.InventoryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final CustomerRepository customerRepository;
  private final InventoryServiceClient inventoryServiceClient;

  @Autowired
  public BookingService(final CustomerRepository customerRepository,
      final InventoryServiceClient inventoryServiceClient) {
    this.customerRepository = customerRepository;
    this.inventoryServiceClient = inventoryServiceClient;
  }

  public BookingResponse createBooking(final BookingRequest request) {
    System.out.println("Create booking called: " + request);
    // check if user exists
    final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
    if (customer == null)
      throw new RuntimeException("User not found");

    // check enough inventory
    // --- get event information to also get Venue information
    final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
    System.out.println(inventoryResponse);

    // create booking

    // send booking to Order Service on a Kafka Topic

    return BookingResponse.builder().build();
  }
}
