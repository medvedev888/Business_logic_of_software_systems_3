package me.ifmo.backend.integration.bitrix.dto;

import java.math.BigDecimal;

public record BitrixDealRequest (
        String title,
        BigDecimal opportunity,
        String currencyId,
        String comments
){
}
