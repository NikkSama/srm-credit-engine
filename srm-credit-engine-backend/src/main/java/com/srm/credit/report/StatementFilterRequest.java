package com.srm.credit.report;

import jakarta.validation.constraints.Size;

import java.time.Instant;

public record StatementFilterRequest(
        @Size(max = 120) String assignor,
        @Size(min = 3, max = 3) String paymentCurrency,
        Instant from,
        Instant to
) {}
