package me.ifmo.backend.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.integration.kafka.event.PaymentCreatedEvent;
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
public class KafkaPaymentCreatedConsumer {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final ObjectMapper objectMapper;
    private final PaymentTransactionService paymentTransactionService;
    private final String paymentCreatedTopic;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public KafkaPaymentCreatedConsumer(
            @Qualifier("paymentCreatedKafkaConsumer") KafkaConsumer<String, String> kafkaConsumer,
            ObjectMapper objectMapper,
            PaymentTransactionService paymentTransactionService,
            @Value("${kafka.topics.payment-created}") String paymentCreatedTopic
    ) {
        this.kafkaConsumer = kafkaConsumer;
        this.objectMapper = objectMapper;
        this.paymentTransactionService = paymentTransactionService;
        this.paymentCreatedTopic = paymentCreatedTopic;
    }

    @PostConstruct
    public void start() {
        executorService.submit(this::listen);
    }


    private void listen() {
        kafkaConsumer.subscribe(List.of(paymentCreatedTopic));

        while (!Thread.currentThread().isInterrupted()) {
            var records = kafkaConsumer.poll(Duration.ofMillis(1000));

            boolean allRecordsProcessedSuccessfully = true;

            for (var record : records) {
                try {
                    PaymentCreatedEvent paymentCreatedEvent =
                            objectMapper.readValue(record.value(), PaymentCreatedEvent.class);

                    log.info(
                            "Received payment created event: paymentId={}, providerPaymentId={}, paymentUrl={}",
                            paymentCreatedEvent.paymentId(),
                            paymentCreatedEvent.providerPaymentId(),
                            paymentCreatedEvent.paymentUrl()
                    );

                    paymentTransactionService.updatePaymentWithProviderData(paymentCreatedEvent);

                } catch (Exception e) {
                    allRecordsProcessedSuccessfully = false;
                    log.error(
                            "Failed to process payment created event. Topic: {}, partition: {}, offset: {}",
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
