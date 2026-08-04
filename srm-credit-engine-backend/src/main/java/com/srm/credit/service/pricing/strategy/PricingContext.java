package com.srm.credit.service.pricing.strategy;

import java.math.BigDecimal;

/**
 * Immutable input handed to a {@link PricingStrategy} so it can decide the
 * effective monthly spread for an operation.
 *
 * @param receivableType name of the receivable type being priced
 * @param faceValue      valor de face
 * @param termMonths     prazo em meses
 * @param baseRate       taxa base mensal informada na operacao
 * @param monthlySpread  spread base do produto (vindo do banco, data-driven)
 */
public record PricingContext(
        String receivableType,
        BigDecimal faceValue,
        int termMonths,
        BigDecimal baseRate,
        BigDecimal monthlySpread
) {
}
