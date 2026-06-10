package me.ifmo.backend.integration.kafka.event;

public record PaymentCreatedEvent(
        Long paymentId,
        String providerPaymentId,
        String paymentUrl
) {
}
