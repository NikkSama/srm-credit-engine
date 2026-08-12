package com.srm.credit.dto;

import com.srm.credit.validation.ValueOfEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Input for a settlement simulation or creation.
 *
 * @param assignor         cedente (originator of the receivable)
 * @param receivableType   name of the receivable type (looked up in the database)
 * @param faceValue        valor de face (must be positive)
 * @param termMonths       prazo em meses
 * @param baseRate         taxa base mensal (e.g. 0.01 = 1% a.m.)
 * @param originalCurrency ISO code of the title currency (e.g. BRL)
 * @param paymentCurrency  ISO code of the payment currency (e.g. USD)
 */
public record SettlementRequest(
        @NotBlank @Size(max = 120) String assignor,
        @NotBlank @Size(max = 60) String receivableType,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 16, fraction = 2) BigDecimal faceValue,
        @NotNull @Min(1) Integer termMonths,
        @NotNull @DecimalMin(value = "0.0") @Digits(integer = 3, fraction = 6) BigDecimal baseRate,
        @NotBlank @ValueOfEnum(enumClass = com.srm.credit.domain.CurrencyCode.class) String originalCurrency,
        @NotBlank @ValueOfEnum(enumClass = com.srm.credit.domain.CurrencyCode.class) String paymentCurrency
) {}
