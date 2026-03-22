package com.example.product_service.dto;

public record ErrorResponse(int status, String message, long timestamp) {}
