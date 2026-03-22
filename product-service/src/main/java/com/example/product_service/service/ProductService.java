package com.example.product_service.service;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.dto.ProductResponse;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok for constructor injection
public class ProductService {

  private final ProductRepository productRepository;
  public static final String PRODUCT_NOT_FOUND = "Product with ID %d not found";

  public ProductResponse saveProduct(ProductRequest request) {
    // 1. Map DTO to Entity
    Product product = new Product();
    product.setName(request.name());
    product.setPrice(request.price());

    // 2. Save to DB
    Product savedProduct = productRepository.save(product);

    // 3. Map Entity back to Response DTO
    return mapToResponse(savedProduct);
  }

  public ProductResponse getProductById(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
    return mapToResponse(product);
  }

  // Helper method to keep code DRY
  private ProductResponse mapToResponse(Product product) {
    return new ProductResponse(product.getId(), product.getName(), product.getPrice());
  }

  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll().stream().map(this::mapToResponse).toList();
  }
}
