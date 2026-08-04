package com.srm.credit.controller;

import com.srm.credit.dto.SettlementRequest;
import com.srm.credit.dto.SettlementResponse;
import com.srm.credit.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettlementService service;

    public SettlementController(SettlementService service) {
        this.service = service;
    }

    /** Stateless simulation - does not persist. */
    @PostMapping("/simulate")
    public SettlementResponse simulate(@Valid @RequestBody SettlementRequest request) {
        return service.simulate(request);
    }

    /** Prices and persists the settlement atomically. */
    @PostMapping
    public ResponseEntity<SettlementResponse> create(@Valid @RequestBody SettlementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
