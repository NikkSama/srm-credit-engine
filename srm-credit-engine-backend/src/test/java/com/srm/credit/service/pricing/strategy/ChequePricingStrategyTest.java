package com.srm.credit.service.pricing.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ChequePricingStrategyTest {

    private final ChequePricingStrategy strategy = new ChequePricingStrategy();

    @Test
    void keepsBaseSpreadWithinThreshold() {
        // term 3 (== threshold) => no premium => base spread 0.025
        PricingContext ctx = new PricingContext(
                "CHEQUE_PRE_DATADO", new BigDecimal("1000.00"), 3,
                BigDecimal.ZERO, new BigDecimal("0.025"));

        assertThat(strategy.resolveSpread(ctx)).isEqualByComparingTo("0.025");
    }

    @Test
    void addsRiskPremiumBeyondThreshold() {
        // term 5 => 2 months over threshold => +0.002 => 0.027
        PricingContext ctx = new PricingContext(
                "CHEQUE_PRE_DATADO", new BigDecimal("1000.00"), 5,
                BigDecimal.ZERO, new BigDecimal("0.025"));

        assertThat(strategy.resolveSpread(ctx)).isEqualByComparingTo("0.027");
    }
}
