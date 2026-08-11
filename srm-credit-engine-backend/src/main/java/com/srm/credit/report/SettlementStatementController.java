package com.srm.credit.report;

import java.time.Instant;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/reports")
public class SettlementStatementController {

    private final SettlementStatementRepository repository;

    public SettlementStatementController(SettlementStatementRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/settlement-statement")
    public PageResult<SettlementStatementRow> statement(
            @Valid StatementFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Generating settlement statement report with filters - assignor: {}, " +
                        "paymentCurrency: {}, from: {}, to: {}, page: {}, size: {}",
                filter.assignor(), filter.paymentCurrency(), filter.from(), filter.to(), page, size);
        return repository.search(filter.assignor(), filter.paymentCurrency(), filter.from(), filter.to(), page, size);
    }
}
