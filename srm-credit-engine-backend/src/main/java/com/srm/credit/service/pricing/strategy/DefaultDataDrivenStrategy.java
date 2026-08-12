package com.srm.credit.service.pricing.strategy;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generic fallback strategy that preserves the original data-driven behaviour:
 * the effective spread is exactly the {@code monthly_spread} configured for the
 * receivable type in the database. Used for any type without a dedicated
 * strategy, so new products can still be added via data only.
 */
@Slf4j
@Component
public class DefaultDataDrivenStrategy implements PricingStrategy {

    /** {@code null} keeps this bean out of the keyed map; it is the fallback. */
    @Override
    public String receivableType() {
        return null;
    }

    @Override
    public BigDecimal resolveSpread(PricingContext ctx) {
        log.debug("Using fallback data-driven spread for type '{}'. Applying base database spread: {}",
                ctx.receivableType(), ctx.monthlySpread());
        return ctx.monthlySpread();
    }
}
