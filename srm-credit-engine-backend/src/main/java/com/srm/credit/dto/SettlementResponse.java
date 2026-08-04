package com.srm.credit.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SettlementResponse(
        Long id,
        String assignor,
        String receivableType,
        BigDecimal faceValue,
        Integer termMonths,
        BigDecimal baseRate,
        BigDecimal appliedSpread,
        String originalCurrency,
        String paymentCurrency,
        boolean crossCurrency,
        BigDecimal exchangeRate,
        BigDecimal presentValue,
        BigDecimal netValuePaid,
        Instant createdAt
) {
}
