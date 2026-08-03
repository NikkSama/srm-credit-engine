package com.srm.credit.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReceivableTypeRequest(
        @NotBlank @Size(min = 1, max = 60) String name,
        @Digits(integer = 3, fraction = 6) BigDecimal monthlySpread) {
}
