package com.example.notification_service.service;

import com.example.notification_service.dto.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventListener {

  @KafkaListener(topics = "order-placed")
  public void handleOrderPlaced(OrderPlacedEvent event) {
    if (event == null) {
      log.warn("Skipping order-placed message with null payload (deserialization likely failed).");
      return;
    }

    log.info(
        "📧 Notification Sent: Order #{} for Product #{} is confirmed!",
        event.orderId(),
        event.productId());
  }
}
