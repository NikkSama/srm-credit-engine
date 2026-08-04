package com.srm.credit.service.pricing.strategy;

import java.math.BigDecimal;

/**
 * Strategy for deciding the effective monthly spread of a receivable type.
 *
 * <p>Implementations are registered as Spring beans and resolved by
 * {@link PricingStrategyResolver}. This keeps the {@code PricingCalculator}
 * a pure math engine while the risk rule per product lives in its own class.
 */
public interface PricingStrategy {

    /**
     * Receivable type name handled by this strategy, or {@code null} when the
     * strategy is the generic data-driven fallback (not keyed by any type).
     */
    String receivableType();

    /** Resolves the monthly spread to apply for the given operation. */
    BigDecimal resolveSpread(PricingContext ctx);
}
