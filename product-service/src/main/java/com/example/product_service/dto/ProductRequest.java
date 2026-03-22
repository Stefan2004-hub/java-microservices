package com.example.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank(message = "Product name cannot be empty")
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,
    @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero")
        Double price) {}
