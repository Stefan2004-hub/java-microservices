package com.example.order_service.client;

import com.example.order_service.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "product-client", url = "http://localhost:8081/products")
@FeignClient(name = "product-service")
public interface ProductClient {
  @GetMapping("/products/{id}")
  ProductResponse getProduct(@PathVariable Long id);
}
