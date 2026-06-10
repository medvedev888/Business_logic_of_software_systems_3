package me.ifmo.backend.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.integration.kafka.event.PaymentStatusChangedEvent;
import me.ifmo.backend.services.impl.PaymentTransactionService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j

@Component
public class KafkaPaymentStatusChangedConsumer {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final ObjectMapper objectMapper;
    private final PaymentTransactionService paymentTransactionService;
    private final String paymentStatusChangedTopic;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public KafkaPaymentStatusChangedConsumer(
            @Qualifier("paymentStatusChangedKafkaConsumer") KafkaConsumer<String, String> kafkaConsumer,
            ObjectMapper objectMapper,
            PaymentTransactionService paymentTransactionService,
            @Value("${kafka.topics.payment-status-changed}") String paymentStatusChangedTopic
    ) {
        this.kafkaConsumer = kafkaConsumer;
        this.objectMapper = objectMapper;
        this.paymentTransactionService = paymentTransactionService;
        this.paymentStatusChangedTopic = paymentStatusChangedTopic;
    }


    @PostConstruct
    public void start() {
        executorService.submit(this::listen);
    }


    private void listen() {
        kafkaConsumer.subscribe(List.of(paymentStatusChangedTopic));

        while (!Thread.currentThread().isInterrupted()) {
            var records = kafkaConsumer.poll(Duration.ofMillis(1000));

            boolean allRecordsProcessedSuccessfully = true;

            for (var record : records) {
                try {
                    PaymentStatusChangedEvent paymentStatusChangedEvent =
                            objectMapper.readValue(record.value(), PaymentStatusChangedEvent.class);

                    log.info(
                            "Received payment status changed event: providerPaymentId={}, status={}, failureReason={}",
                            paymentStatusChangedEvent.providerPaymentId(),
                            paymentStatusChangedEvent.status(),
                            paymentStatusChangedEvent.failureReason()
                    );

                    switch (paymentStatusChangedEvent.status()) {
                        case "PAID" -> paymentTransactionService.processPaidWebhook(
                                paymentStatusChangedEvent.providerPaymentId());

                        case "FAILED" -> paymentTransactionService.processFailedWebhook(
                                paymentStatusChangedEvent.providerPaymentId(),
                                paymentStatusChangedEvent.failureReason()
                        );

                        default -> log.warn(
                                "Unknown payment status received from Kafka: providerPaymentId={}, status={}",
                                paymentStatusChangedEvent.providerPaymentId(),
                                paymentStatusChangedEvent.status()
                        );
                    }

                } catch (Exception e) {
                    allRecordsProcessedSuccessfully = false;
                    log.error(
                            "Failed to process payment status changed event. Topic: {}, partition: {}, offset: {}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            e
                    );
                    break;
                }
            }

            if (allRecordsProcessedSuccessfully && !records.isEmpty()) {
                kafkaConsumer.commitSync();
            }
        }
    }


    @PreDestroy
    public void stop() {
        kafkaConsumer.wakeup();
        executorService.shutdown();
    }


}
