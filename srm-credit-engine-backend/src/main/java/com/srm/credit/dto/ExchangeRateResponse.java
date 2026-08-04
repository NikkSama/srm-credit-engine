package com.srm.credit.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateResponse(
        Long id,
        String baseCurrency,
        String quoteCurrency,
        BigDecimal rate,
        Instant validAt
) {
}
