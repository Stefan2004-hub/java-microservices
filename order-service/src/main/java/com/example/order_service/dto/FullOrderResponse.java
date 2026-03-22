package com.example.order_service.dto;

import com.example.order_service.client.dto.ProductResponse;

public record FullOrderResponse(
    Long orderId,
    Integer quantity,
    Double totalPrice,
    ProductResponse product // This nested object comes from the Product Service
    ) {}
