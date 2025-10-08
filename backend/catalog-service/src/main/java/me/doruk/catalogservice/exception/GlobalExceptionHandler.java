package me.doruk.catalogservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", ex.getStatusCode().value());
    error.put("message", ex.getReason());
    return new ResponseEntity<>(error, ex.getStatusCode());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.BAD_REQUEST.value());
    error.put("message", ex.getConstraintViolations()
        .stream()
        .map(v -> v.getMessage())
        .reduce((m1, m2) -> m1 + "; " + m2)
        .orElse("Validation error"));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.BAD_REQUEST.value());

    String message = null;
    Throwable root = ex.getRootCause();
    if (root != null && root.getMessage() != null) {
      message = root.getMessage();
    } else if (ex.getMessage() != null) {
      message = ex.getMessage();
    }

    if (message != null && message.contains("Duplicate entry") && message.contains("customer.email")) {
      error.put("message", "Email address already exists.");
    } else {
      error.put("message", "Data integrity violation.");
    }
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.BAD_REQUEST.value());
    String message = "Validation error";
    if (ex.getBindingResult() != null) {
      message = ex.getBindingResult().getFieldErrors()
          .stream()
          .map(fieldError -> fieldError.getDefaultMessage())
          .reduce((m1, m2) -> m1 + "; " + m2)
          .orElse("Validation error");
    }
    error.put("message", message);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    error.put("message", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}