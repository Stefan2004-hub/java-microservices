package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.client.dto.ProductResponse;
import com.example.order_service.dto.FullOrderResponse;
import com.example.order_service.dto.OrderPlacedEvent;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductClient productClient; // Our Feign Client
  private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

  public OrderResponse createOrder(OrderRequest request) {
    // 1. Call Product Service via Feign (Synchronous Communication)
    // If product doesn't exist, this throws an exception (which we'll handle)
    ProductResponse product = fetchProduct(request.productId());

    // 2. Calculate Total Price
    Double total = calculateTotalPrice(product, request.quantity());
    // 3. Map to Entity and Save
    Order savedOrder = saveOrder(request.productId(), request.quantity(), total);

    // 3. ASYNCHRONOUS STEP: Send to Kafka
    try {
      OrderPlacedEvent event =
          new OrderPlacedEvent(
              savedOrder.getId(), savedOrder.getProductId(), savedOrder.getQuantity());
      log.info("Sending OrderPlacedEvent to Kafka for Order ID: {}", savedOrder.getId());
      kafkaTemplate.send("order-placed", event);
    } catch (Exception e) {
      log.error("Failed to send Kafka message, but order was saved!", e);
      // We don't throw an exception here because the order is already saved.
      // This is the "Eventually Consistent" trade-off.
    }

    // 4. Return Response DTO
    return mapToOrderResponse(savedOrder);
  }

  // Helper method to keep code DRY
  private ProductResponse fetchProduct(Long productId) {
    try {
      return productClient.getProduct(productId);
    } catch (Exception e) {
      throw new ResourceNotFoundException(
          "Cannot place order. Product not found with ID: " + productId);
    }
  }

  // Helper method to keep code DRY
  private Double calculateTotalPrice(ProductResponse product, Integer quantity) {
    return product.price() * quantity;
  }

  // Helper method to keep code DRY
  private Order saveOrder(Long productId, Integer quantity, Double totalPrice) {
    Order order = new Order();
    order.setProductId(productId);
    order.setQuantity(quantity);
    order.setTotalPrice(totalPrice);
    return orderRepository.save(order);
  }

  // Helper method to keep code DRY
  private OrderResponse mapToOrderResponse(Order order) {
    return new OrderResponse(
        order.getId(), order.getProductId(), order.getQuantity(), order.getTotalPrice());
  }

  public FullOrderResponse getOrderDetails(Long orderId) {
    // 1. Get Order from local Order DB
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    // 2. Get Product details from Product Service (via Feign)
    ProductResponse product = fetchProduct(order.getProductId());

    // 3. Combine them into the Aggregated DTO
    return new FullOrderResponse(
        order.getId(), order.getQuantity(), order.getTotalPrice(), product);
  }
}
