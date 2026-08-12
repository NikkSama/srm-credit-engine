package com.srm.credit.service.pricing.strategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves the {@link PricingStrategy} for a receivable type.
 *
 * <p>Spring injects every {@code PricingStrategy} bean. Type-specific
 * strategies are indexed by {@link PricingStrategy#receivableType()}; any type
 * without a dedicated strategy falls back to the data-driven default, so the
 * original data-only extensibility is preserved.
 */
@Slf4j
@Component
public class PricingStrategyResolver {

    private final Map<String, PricingStrategy> byType;
    private final PricingStrategy defaultStrategy;

    public PricingStrategyResolver(List<PricingStrategy> strategies,
                                   DefaultDataDrivenStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        this.byType = strategies.stream()
                .filter(strategy -> strategy.receivableType() != null)
                .collect(Collectors.toMap(PricingStrategy::receivableType, strategy -> strategy));

        log.debug("PricingStrategyResolver initialized. Mapped custom strategies for types: {}", byType.keySet());
    }

    public PricingStrategy resolve(String receivableType) {
        PricingStrategy strategy = byType.get(receivableType);

        if (strategy != null) {
            log.debug("Custom pricing strategy found for receivable type: {}", receivableType);
            return strategy;
        }

        log.debug("No custom strategy found for type: {}. Falling back to default data-driven strategy", receivableType);
        return defaultStrategy;
    }
}
