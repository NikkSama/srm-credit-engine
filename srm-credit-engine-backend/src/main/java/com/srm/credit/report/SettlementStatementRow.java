package com.srm.credit.report;

import java.math.BigDecimal;
import java.time.Instant;

public record SettlementStatementRow(
        Long id,
        String assignor,
        String receivableType,
        BigDecimal faceValue,
        BigDecimal presentValue,
        BigDecimal netValuePaid,
        String originalCurrency,
        String paymentCurrency,
        Instant createdAt
) {
}
