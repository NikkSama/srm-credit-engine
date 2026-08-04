package com.srm.credit.service.pricing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {

    /** Working precision for intermediate steps. */
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);
    /** Monetary scale for persisted/returned amounts. */
    private static final int MONEY_SCALE = 2;

    public PricingResult price(
            BigDecimal faceValue,
            int termMonths,
            BigDecimal baseRate,
            BigDecimal spread,
            BigDecimal exchangeRate) {

        // (1 + baseRate + spread)
        BigDecimal ratePerMonth = BigDecimal.ONE.add(baseRate, MC).add(spread, MC);
        // ^term  (term is a positive integer number of months)
        BigDecimal discountFactor = ratePerMonth.pow(termMonths, MC);

        BigDecimal presentValue = faceValue.divide(discountFactor, MC);

        BigDecimal netValue = presentValue;
        if (exchangeRate != null) {
            netValue = presentValue.multiply(exchangeRate, MC);
        }

        return new PricingResult(
                spread,
                presentValue.setScale(MONEY_SCALE, RoundingMode.HALF_EVEN),
                netValue.setScale(MONEY_SCALE, RoundingMode.HALF_EVEN)
        );
    }
}
