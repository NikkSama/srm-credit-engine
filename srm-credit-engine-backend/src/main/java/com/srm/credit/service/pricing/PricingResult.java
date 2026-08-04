package com.srm.credit.service.pricing;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal appliedSpread,
        BigDecimal presentValue,
        BigDecimal netValuePaid
) {
}