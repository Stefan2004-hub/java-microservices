package com.example.order_service.exception;

import com.example.order_service.dto.ErrorResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(FeignException.NotFound.class)
  public ResponseEntity<ErrorResponse> handleFeignNotFound(FeignException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Product not found in Product Service",
            System.currentTimeMillis());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }
}
