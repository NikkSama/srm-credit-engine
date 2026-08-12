package com.srm.credit.controller;

import com.srm.credit.dto.ReceivableTypeRequest;
import com.srm.credit.dto.ReceivableTypeResponse;
import com.srm.credit.service.ReceivableTypeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/receivable-types")
public class ReceivableTypeController {

    private final ReceivableTypeService service;

    public ReceivableTypeController(ReceivableTypeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReceivableTypeResponse>> list() {
        log.info("Listing all receivable types");
        return ResponseEntity.ok().body(service.list());
    }

    @PostMapping
    public ResponseEntity<ReceivableTypeResponse> create(@Valid @RequestBody ReceivableTypeRequest request) {
        log.info("Creating a receivable type for request: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
