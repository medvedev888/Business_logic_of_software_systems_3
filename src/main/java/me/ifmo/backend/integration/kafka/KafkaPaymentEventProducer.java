package me.ifmo.backend.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.exceptions.KafkaEventPublishingException;
import me.ifmo.backend.integration.kafka.event.PaymentCreateRequestedEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor

@Component
public class KafkaPaymentEventProducer {
    private final KafkaProducer<String, String> kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.payment-create-requested}")
    private String paymentCreateRequestedTopic;


    public void sendPaymentCreateRequested(PaymentCreateRequestedEvent event) {
        String key = event.paymentId().toString();
        String value;

        try {
            value = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new KafkaEventPublishingException("Failed to serialize payment create requested event", e);
        }

        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(paymentCreateRequestedTopic, key, value);

        kafkaProducer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to publish payment create requested event: " + exception.getMessage());
                return;
            }

            log.info("Published payment create requested event. Topic: "
                    + metadata.topic()
                    + ", partition: "
                    + metadata.partition()
                    + ", offset: "
                    + metadata.offset());
        });
    }

}
