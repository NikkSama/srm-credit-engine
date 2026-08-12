package com.srm.credit.dto;

import com.srm.credit.domain.CurrencyCode;
import com.srm.credit.validation.ValueOfEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ExchangeRateRequest(
        @NotBlank  @ValueOfEnum(enumClass = CurrencyCode.class) String baseCurrency,
        @NotBlank @ValueOfEnum(enumClass = CurrencyCode.class) String quoteCurrency,
        @NotNull @DecimalMin(value = "0.00000001") @Digits(integer = 9, fraction = 8) BigDecimal rate) {
}
