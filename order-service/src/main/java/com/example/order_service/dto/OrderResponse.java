package com.example.order_service.dto;

public record OrderResponse(Long id, Long productId, Integer quantity, Double totalPrice) {}
