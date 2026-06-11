package me.ifmo.backend.batch;

import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import me.ifmo.backend.entities.enums.PaymentStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentExpirationProcessor implements ItemProcessor<Payment, Payment> {

    @Override
    public Payment process(Payment payment) {
        LocalDateTime now = LocalDateTime.now();

        payment.setStatus(PaymentStatus.EXPIRED);
        payment.setUpdatedAt(now);

        var enrollment = payment.getEnrollment();

        if (enrollment.getStatus() == EnrollmentStatus.PENDING_PAYMENT) {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_EXPIRED);
            enrollment.setUpdatedAt(now);
        }

        return payment;
    }

}
