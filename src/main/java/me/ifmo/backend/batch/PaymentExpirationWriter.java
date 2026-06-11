package me.ifmo.backend.batch;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor

@Component
public class PaymentExpirationWriter implements ItemWriter<Payment> {
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void write(Chunk<? extends Payment> chunk) {
        for (Payment payment : chunk) {
            enrollmentRepository.save(payment.getEnrollment());
            paymentRepository.save(payment);
        }
    }

}
