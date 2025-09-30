package me.doruk.bookingService.controller;

import me.doruk.bookingService.request.BookingRequest;
import me.doruk.bookingService.request.UserCreateRequest;
import me.doruk.bookingService.response.BookingResponse;
import me.doruk.bookingService.response.UserResponse;
import me.doruk.bookingService.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class BookingController {

  private final BookingService bookingService;

  @Autowired
  public BookingController(final BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping("/booking/users")
  public @ResponseBody List<UserResponse> getUsers() {
    System.out.println("GET /api/v1/booking/users called");
    return bookingService.GetAllUsers();
  }

  @PostMapping(value = "/booking/add-user", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
    System.out.println("POST /api/v1/booking/add-user called");
    UserResponse createdUser = bookingService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @PostMapping(value = "/booking", consumes = "application/json", produces = "application/json")
  public BookingResponse createBooking(@RequestBody BookingRequest request) {
    System.out.println("POST /api/v1/booking called");
    return bookingService.createBooking(request);
  }
}
