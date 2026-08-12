package com.srm.credit.service.pricing.strategy;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Pricing rule for post-dated cheques ({@code CHEQUE_PRE_DATADO}).
 *
 * <p>On top of the product's base spread, adds a risk premium that grows with
 * the term: {@value #RISK_PREMIUM_PER_MONTH} per month beyond
 * {@value #TERM_THRESHOLD} months, reflecting the higher default risk of
 * longer-dated cheques.
 */
@Slf4j
@Component
public class ChequePricingStrategy implements PricingStrategy {

    private static final int TERM_THRESHOLD = 3;
    private static final BigDecimal RISK_PREMIUM_PER_MONTH = new BigDecimal("0.001");

    @Override
    public String receivableType() {
        return "CHEQUE_PRE_DATADO";
    }

    @Override
    public BigDecimal resolveSpread(PricingContext ctx) {
        BigDecimal extra = BigDecimal.ZERO;
        if (ctx.termMonths() > TERM_THRESHOLD) {
            int overThresholdMonths = ctx.termMonths() - TERM_THRESHOLD;
            extra = RISK_PREMIUM_PER_MONTH.multiply(BigDecimal.valueOf(overThresholdMonths));

            log.debug("Cheque risk premium applied - Term: {} months, Over threshold: {} months, Extra spread added: {}",
                    ctx.termMonths(), overThresholdMonths, extra);
        } else {
            log.debug("Cheque term {} months is within safety threshold (<= {}). No risk premium added",
                    ctx.termMonths(), TERM_THRESHOLD);
        }

        BigDecimal totalSpread = ctx.monthlySpread().add(extra);
        log.debug("Cheque pricing final spread calculated: {} (Base: {})", totalSpread, ctx.monthlySpread());

        return totalSpread;
    }
}
