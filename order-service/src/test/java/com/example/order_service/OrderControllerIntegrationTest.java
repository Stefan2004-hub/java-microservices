package com.example.order_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BaseIntegrationTest;
import com.example.order_service.client.ProductClient;
import com.example.order_service.client.dto.ProductResponse;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class OrderControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private OrderRepository orderRepository;

  @MockitoBean private ProductClient productClient;

  @BeforeEach
  void cleanData() {
    orderRepository.deleteAll();
  }

  @Test
  void shouldPlaceOrder() throws Exception {
    OrderRequest request = new OrderRequest(1L, 2);
    String requestJson = objectMapper.writeValueAsString(request);

    when(productClient.getProduct(any(Long.class))).thenReturn(new ProductResponse(1L, "Keyboard", 50.0));

    mockMvc
        .perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.productId").value(1))
        .andExpect(jsonPath("$.quantity").value(2))
        .andExpect(jsonPath("$.totalPrice").value(100.0));
  }
}
