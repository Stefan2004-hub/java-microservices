package com.example.order_service.dto;

public record ErrorResponse(int status, String message, long timestamp) {}
