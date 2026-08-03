package com.srm.credit.report;

import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class SettlementStatementController {

    private final SettlementStatementRepository repository;

    public SettlementStatementController(SettlementStatementRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/settlement-statement")
    public PageResult<SettlementStatementRow> statement(
            @RequestParam(required = false) String assignor,
            @RequestParam(required = false) String paymentCurrency,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return repository.search(assignor, paymentCurrency, from, to, page, size);
    }
}
