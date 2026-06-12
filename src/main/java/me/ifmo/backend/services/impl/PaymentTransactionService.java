package me.ifmo.backend.services.impl;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.integration.kafka.event.PaymentCreatedEvent;
import me.ifmo.backend.services.PaymentCallbackService;
import me.ifmo.backend.services.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final TransactionTemplate transactionTemplate;
    private final PaymentCallbackService paymentCallbackService;
    private final PaymentService paymentService;
    private final BitrixIntegrationService bitrixIntegrationService;

    public Payment createPayment(Long enrollmentId) {
        return transactionTemplate.execute(status -> {
            try {
                return paymentService.createPayment(enrollmentId);
            } catch (RuntimeException exception) {
                status.setRollbackOnly();
                throw exception;
            }
        });
    }


    public void updatePaymentWithProviderData(PaymentCreatedEvent event) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                paymentService.updatePaymentWithProviderData(event);
            } catch (RuntimeException exception) {
                status.setRollbackOnly();
                throw exception;
            }
        });
    }


    public void processPaidWebhook(String providerPaymentId) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                paymentCallbackService.markPaymentAsPaid(providerPaymentId);
            } catch (RuntimeException exception) {
                status.setRollbackOnly();
                throw exception;
            }
        });
        bitrixIntegrationService.createPaidCourseDeal(providerPaymentId);
    }


    public void processFailedWebhook(String providerPaymentId, String failureReason) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                paymentCallbackService.markPaymentAsFailed(providerPaymentId, failureReason);
            } catch (RuntimeException exception) {
                status.setRollbackOnly();
                throw exception;
            }
        });
    }

}