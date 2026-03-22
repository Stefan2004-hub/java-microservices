package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.client.dto.ProductResponse;
import com.example.order_service.dto.FullOrderResponse;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductClient productClient; // Our Feign Client

  public OrderResponse createOrder(OrderRequest request) {
    // 1. Call Product Service via Feign (Synchronous Communication)
    // If product doesn't exist, this throws an exception (which we'll handle)
    ProductResponse product = fetchProduct(request.productId());

    // 2. Calculate Total Price
    Double total = calculateTotalPrice(product, request.quantity());
    // 3. Map to Entity and Save
    Order order = new Order();
    order.setProductId(request.productId());
    order.setQuantity(request.quantity());
    order.setTotalPrice(total);

    Order savedOrder = saveOrder(request.productId(), request.quantity(), total);

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
