package com.srm.credit.dto;

import java.math.BigDecimal;

/**
 * Catalog entry describing a receivable type and its risk spread.
 * Fed to the operator panel so products can be added via database only.
 */
public record ReceivableTypeResponse(
        Long id,
        String name,
        BigDecimal monthlySpread
) {
}
