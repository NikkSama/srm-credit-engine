package com.srm.credit.controller;

import com.srm.credit.dto.ExchangeRateRequest;
import com.srm.credit.dto.ExchangeRateResponse;
import com.srm.credit.mapper.ExchangeRateMapper;
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
    private final ExchangeRateMapper mapper;

    public ExchangeRateController(ExchangeRateService service, ExchangeRateMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> create(@Valid @RequestBody ExchangeRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.upsert(request)));
    }

    @GetMapping
    public List<ExchangeRateResponse> list() {
        return service.listAll().stream().map(mapper::toResponse).toList();
    }
}

