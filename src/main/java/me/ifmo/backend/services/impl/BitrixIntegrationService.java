package me.ifmo.backend.services.impl;

import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealRequest;
import me.ifmo.backend.integration.bitrix.dto.BitrixDealResponse;
import me.ifmo.backend.integration.bitrix.jca.BitrixConnection;
import me.ifmo.backend.integration.bitrix.jca.BitrixConnectionFactory;
import me.ifmo.backend.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor

@Service
public class BitrixIntegrationService {
    private final BitrixConnectionFactory bitrixConnectionFactory;
    private final PaymentRepository paymentRepository;


    @Transactional(readOnly = true)
    public void createPaidCourseDeal(String providerPaymentId) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found by providerPaymentId: " + providerPaymentId
                ));
        Enrollment enrollment = payment.getEnrollment();
        Course course = enrollment.getCourse();

        String title = "Course payment: " + course.getTitle();
        String comments = "Payment ID: " + payment.getId() + "\n"
                + "Provider payment ID: " + payment.getProviderPaymentId() + "\n"
                + "Enrollment ID: " + enrollment.getId() + "\n"
                + "Course ID: " + course.getId() + "\n"
                + "Course title: " + course.getTitle();

        BitrixDealRequest request = new BitrixDealRequest(
                title,
                payment.getAmount(),
                payment.getCurrency(),
                comments
        );

        BitrixConnection connection = null;

        try {
            connection = (BitrixConnection) bitrixConnectionFactory.getConnection();

            BitrixDealResponse response = connection.createDeal(request);

            log.info(
                    "Bitrix deal created successfully: dealId={}, paymentId={}",
                    response.dealId(),
                    payment.getId()
            );
        } catch (ResourceException exception) {
            log.error(
                    "Failed to create Bitrix deal for paymentId={}, providerPaymentId={}",
                    payment.getId(),
                    providerPaymentId,
                    exception
            );
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (ResourceException exception) {
                    log.warn("Failed to close Bitrix connection", exception);
                }
            }
        }
    }

}
