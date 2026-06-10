package me.ifmo.backend.services;

import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.integration.kafka.event.PaymentCreatedEvent;

public interface PaymentService {
    Payment createPayment(Long enrollmentId);

    void updatePaymentWithProviderData(PaymentCreatedEvent event);

    Payment getPaymentById(Long id);

    Payment getPaymentByEnrollmentId(Long enrollmentId);
}