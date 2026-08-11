package com.srm.credit.service.pricing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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

        log.debug("Executing pricing engine - FaceValue: {}, Terms: {} months, BaseRate: {}, Spread: {}, ExchangeRate: {}",
                faceValue, termMonths, baseRate, spread, exchangeRate);

        // (1 + baseRate + spread)
        BigDecimal ratePerMonth = BigDecimal.ONE.add(baseRate, MC).add(spread, MC);
        // ^term  (term is a positive integer number of months)
        BigDecimal discountFactor = ratePerMonth.pow(termMonths, MC);

        BigDecimal presentValue = faceValue.divide(discountFactor, MC);

        log.trace("Intermediate math details - RatePerMonth: {}, DiscountFactor: {}, PresentValue (unscaled): {}",
                ratePerMonth, discountFactor, presentValue);

        BigDecimal netValue = presentValue;
        if (exchangeRate != null) {
            netValue = presentValue.multiply(exchangeRate, MC);
            log.trace("Exchange rate applied. NetValue updated to (unscaled): {}", netValue);
        }

        PricingResult result = new PricingResult(
                spread,
                presentValue.setScale(MONEY_SCALE, RoundingMode.HALF_EVEN),
                netValue.setScale(MONEY_SCALE, RoundingMode.HALF_EVEN)
        );

        log.debug("Pricing calculation completed successfully - Formatted PV: {}, Formatted NetValue: {}",
                result.presentValue(), result.netValuePaid());

        return result;
    }
}
