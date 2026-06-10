package me.ifmo.backend.integration.kafka.event;

import java.math.BigDecimal;

public record PaymentCreateRequestedEvent(
        Long paymentId,
        Long enrollmentId,
        BigDecimal amount,
        String currency
) {
}