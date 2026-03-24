package com.example.order_service.dto;

public record OrderPlacedEvent(Long orderId, Long productId, Integer quantity) {}
