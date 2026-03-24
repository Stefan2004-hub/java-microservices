package com.example.notification_service.dto;

public record OrderPlacedEvent(Long orderId, Long productId, Integer quantity) {}
