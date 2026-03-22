package com.example.product_service.exception;

import com.example.product_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(), ex.getMessage(), System.currentTimeMillis());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  // You can add more handlers here for Validation, Security, etc.
}
