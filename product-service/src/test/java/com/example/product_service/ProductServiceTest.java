package com.example.product_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.dto.ProductResponse;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private ProductService productService;

  @Test
  void shouldSaveProductSuccessfully() {
    // Arrange
    ProductRequest request = new ProductRequest("Monitor", 300.0);
    Product savedProduct = new Product(1L, "Monitor", 300.0);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // Act
    ProductResponse response = productService.saveProduct(request);

    // Assert
    assertNotNull(response.id());
    assertEquals("Monitor", response.name());
    verify(productRepository, times(1)).save(any(Product.class));
  }
}
