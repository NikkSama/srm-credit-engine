package com.srm.credit.service.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {

    private PricingCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PricingCalculator();
    }

    @Test
    void appliesDuplicataSpreadOnSingleCurrency() {
        // baseRate 0, spread 1.5%, term 1 => PV = 1000 / 1.015 = 985.22
        PricingResult result = calculator.price(
                new BigDecimal("1000.00"), 1, BigDecimal.ZERO,
                new BigDecimal("0.015"), null);

        assertThat(result.appliedSpread()).isEqualByComparingTo("0.015");
        assertThat(result.presentValue()).isEqualByComparingTo("985.22");
        assertThat(result.netValuePaid()).isEqualByComparingTo("985.22");
    }

    @Test
    void appliesChequeSpreadOverMultipleMonths() {
        // baseRate 0, spread 2.5%, term 3 => PV = 1000 / 1.025^3 = 928.60
        PricingResult result = calculator.price(
                new BigDecimal("1000.00"), 3, BigDecimal.ZERO,
                new BigDecimal("0.025"), null);

        assertThat(result.appliedSpread()).isEqualByComparingTo("0.025");
        assertThat(result.presentValue()).isEqualByComparingTo("928.60");
    }

    @Test
    void appliesExchangeRateOnCrossCurrency() {
        // PV in BRL then converted BRL->USD at 0.185 (approx 1/5.40)
        PricingResult result = calculator.price(
                new BigDecimal("1000.00"), 1, BigDecimal.ZERO,
                new BigDecimal("0.015"), new BigDecimal("0.185"));

        // 985.2216... * 0.185 = 182.27
        assertThat(result.presentValue()).isEqualByComparingTo("985.22");
        assertThat(result.netValuePaid()).isEqualByComparingTo("182.27");
    }

    @Test
    void combinesBaseRateAndSpread() {
        // baseRate 1%, spread 1.5% => 2.5% per month, term 2
        // PV = 1000 / 1.025^2 = 951.81
        PricingResult result = calculator.price(
                new BigDecimal("1000.00"), 2, new BigDecimal("0.01"),
                new BigDecimal("0.015"), null);

        assertThat(result.presentValue()).isEqualByComparingTo("951.81");
    }
}
