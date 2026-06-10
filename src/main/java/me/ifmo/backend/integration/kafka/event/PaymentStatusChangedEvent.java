package me.ifmo.backend.integration.kafka.event;

public record PaymentStatusChangedEvent(
        String providerPaymentId,
        String status,
        String failureReason
) {
}
