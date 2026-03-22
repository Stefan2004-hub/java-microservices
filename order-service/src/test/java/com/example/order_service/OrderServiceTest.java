package com.example.order_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.order_service.client.ProductClient;
import com.example.order_service.client.dto.ProductResponse;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private ProductClient productClient;

  @InjectMocks private OrderService orderService;

  @Test
  void shouldCreateOrderSuccessfully() {
    OrderRequest request = new OrderRequest(1L, 2);
    ProductResponse productResponse = new ProductResponse(1L, "Keyboard", 50.0);
    Order savedOrder = new Order(10L, 1L, 2, 100.0);

    when(productClient.getProduct(1L)).thenReturn(productResponse);
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    OrderResponse response = orderService.createOrder(request);

    assertNotNull(response.id());
    assertEquals(10L, response.id());
    assertEquals(1L, response.productId());
    assertEquals(2, response.quantity());
    assertEquals(100.0, response.totalPrice());
    verify(productClient, times(1)).getProduct(1L);
    verify(orderRepository, times(1)).save(any(Order.class));
  }
}
