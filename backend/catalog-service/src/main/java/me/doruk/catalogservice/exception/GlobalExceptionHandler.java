package me.doruk.catalogservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {

    String message = ex.getConstraintViolations()
        .stream()
        .map(v -> v.getMessage())
        .reduce((m1, m2) -> m1 + "; " + m2)
        .orElse("Validation error");

    ApiErrorResponse error = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        message);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

    String message = null;
    Throwable root = ex.getRootCause();
    if (root != null && root.getMessage() != null) {
      message = root.getMessage();
    } else if (ex.getMessage() != null) {
      message = ex.getMessage();
    }

    if (message != null && message.contains("Duplicate entry") && message.contains("customer.email")) {
      message = "Email address already exists.";
    } else {
      message = "Data integrity violation.";
    }

    ApiErrorResponse error = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        message);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    String message = "Validation error";
    if (ex.getBindingResult() != null) {
      message = ex.getBindingResult().getFieldErrors()
          .stream()
          .map(fieldError -> fieldError.getDefaultMessage())
          .reduce((m1, m2) -> m1 + "; " + m2)
          .orElse("Validation error");
    }

    ApiErrorResponse error = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        message);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
