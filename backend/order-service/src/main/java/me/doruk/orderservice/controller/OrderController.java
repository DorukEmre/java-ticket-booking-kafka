package me.doruk.orderservice.controller;

import me.doruk.orderservice.request.PaymentRequest;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.response.OrderResponse;
import me.doruk.orderservice.response.UserResponse;
import me.doruk.orderservice.service.OrderQueryService;
import me.doruk.orderservice.service.PaymentService;
import me.doruk.orderservice.service.UserService;
import me.doruk.ticketingcommonlibrary.model.ApiErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class OrderController {

  private final OrderQueryService orderQueryService;
  private final PaymentService paymentService;
  private final UserService userService;

  // Public

  @Operation(summary = "Get order", description = "Retrieves order information by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Order retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping("/orders/{orderId}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") String orderId) {
    System.out.println("GET /api/v1/orders/{orderId} called");
    return orderQueryService.getOrderById(orderId);
  }

  @Operation(summary = "List all orders for given customer", description = "Retrieves all orders by the customer ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping("/users/id/{customerId}/orders")
  public ResponseEntity<List<OrderResponse>> listOrdersByUserId(@PathVariable("customerId") Long customerId) {
    System.out.println("GET /api/v1/users/{customerId}/orders called");
    return orderQueryService.listOrdersByUserId(customerId);
  }

  @Operation(summary = "List all orders for given email", description = "Retrieves all orders by the customer's email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @GetMapping("/users/email/{email}/orders")
  public ResponseEntity<List<OrderResponse>> listOrdersByEmail(@PathVariable("email") @Email String email) {
    System.out.println("GET /api/v1/users/{email}/orders called");
    return orderQueryService.listOrdersByEmail(email);
  }

  @Operation(summary = "Process payment", description = "Processes payment for the specified order.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment processed successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Order is not pending payment; Email cannot be blank; Name cannot be blank", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @PostMapping(value = "/orders/{orderId}/payment", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Void> processPayment(@PathVariable("orderId") String orderId,
      @RequestBody PaymentRequest request) {
    System.out.println("POST /api/v1/orders/{orderId}/payment called");
    return paymentService.processPayment(orderId, request);
  }

  // Admin

  @Operation(hidden = true, description = "Admin use. Lists all orders.")
  // @ApiResponses(value = {
  // @ApiResponse(responseCode = "", description = ""),
  // @ApiResponse(responseCode = "", description = "")
  // })
  @GetMapping("/orders")
  public List<OrderResponse> getOrders() {
    System.out.println("GET /api/v1/orders called");
    return orderQueryService.listAllOrders();
  }

  @Operation(hidden = true, description = "Admin use. Lists all users.")
  // @ApiResponses(value = {
  // @ApiResponse(responseCode = "", description = ""),
  // @ApiResponse(responseCode = "", description = "")
  // })
  @GetMapping("/users")
  public List<UserResponse> getUsers() {
    System.out.println("GET /api/v1/users called");
    return userService.listAllUsers();
  }

  @Operation(hidden = true, description = "Admin use. Creates a new user.")
  // @ApiResponses(value = {
  // @ApiResponse(responseCode = "", description = ""),
  // @ApiResponse(responseCode = "", description = "")
  // })
  @PostMapping(value = "/users/new", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
    System.out.println("POST /api/v1/users/new called");
    UserResponse createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

}
