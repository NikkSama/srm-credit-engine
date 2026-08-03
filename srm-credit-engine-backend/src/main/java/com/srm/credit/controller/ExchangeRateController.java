package com.srm.credit.controller;

import com.srm.credit.domain.ExchangeRate;
import com.srm.credit.dto.ExchangeRateRequest;
import com.srm.credit.dto.ExchangeRateResponse;
import com.srm.credit.service.ExchangeRateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> create(@Valid @RequestBody ExchangeRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(service.upsert(request)));
    }

    @GetMapping
    public List<ExchangeRateResponse> list() {
        return service.listAll().stream().map(this::toResponse).toList();
    }

    private ExchangeRateResponse toResponse(ExchangeRate rate) {
        return new ExchangeRateResponse(
                rate.getId(),
                rate.getBaseCurrency().getCode(),
                rate.getQuoteCurrency().getCode(),
                rate.getRate(),
                rate.getValidAt());
    }
}
