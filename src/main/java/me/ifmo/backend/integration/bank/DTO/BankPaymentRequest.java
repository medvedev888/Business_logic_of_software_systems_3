package me.ifmo.backend.integration.bank.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankPaymentRequest {
    private Long enrollmentId;
    private BigDecimal amount;
    private String currency;
    private String callbackUrl;
}