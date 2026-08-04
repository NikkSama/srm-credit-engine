package com.srm.credit.service.pricing.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PricingStrategyResolverTest {

    private final DefaultDataDrivenStrategy defaultStrategy = new DefaultDataDrivenStrategy();
    private final ChequePricingStrategy cheque = new ChequePricingStrategy();
    private final PricingStrategyResolver resolver =
            new PricingStrategyResolver(List.of(defaultStrategy, cheque), defaultStrategy);

    @Test
    void resolvesTypeSpecificStrategy() {
        assertThat(resolver.resolve("CHEQUE_PRE_DATADO")).isSameAs(cheque);
    }

    @Test
    void fallsBackToDefaultForUnmappedType() {
        assertThat(resolver.resolve("DUPLICATA_MERCANTIL")).isSameAs(defaultStrategy);
    }

    @Test
    void fallsBackToDefaultForUnknownType() {
        assertThat(resolver.resolve("SOMETHING_NEW")).isSameAs(defaultStrategy);
    }
}
