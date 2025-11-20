package me.doruk.cartservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import me.doruk.ticketingcommonlibrary.model.ApiErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {

    ApiErrorResponse error = new ApiErrorResponse(
        ex.getStatusCode().value(),
        ex.getReason());

    return ResponseEntity.status(ex.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {

    ApiErrorResponse error = new ApiErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

}
