package com.srm.credit.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record ReceivableTypeRequest(
        @NotBlank @Size(min = 1, max = 60) @Pattern(regexp = "^[A-Za-z0-9À-ÿ _]+$", message = "Name contains invalid characters") String name,
        @NotNull @Digits(integer = 3, fraction = 6) BigDecimal monthlySpread) {
}
