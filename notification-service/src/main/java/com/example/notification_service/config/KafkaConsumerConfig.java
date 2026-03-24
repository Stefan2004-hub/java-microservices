package com.example.notification_service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

  @Bean
  public DefaultErrorHandler kafkaErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(0L, 0L));
    errorHandler.addNotRetryableExceptions(
        SerializationException.class, DeserializationException.class);
    errorHandler.setRetryListeners(
        (record, ex, deliveryAttempt) ->
            log.error(
                "Skipping invalid Kafka record after {} attempt(s). topic={}, partition={}, offset={}, reason={}",
                deliveryAttempt,
                record != null ? record.topic() : "unknown",
                record != null ? record.partition() : -1,
                record != null ? record.offset() : -1,
                ex.getMessage()));
    return errorHandler;
  }
}
