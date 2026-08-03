package com.srm.credit.controller;

import com.srm.credit.dto.ReceivableTypeRequest;
import com.srm.credit.dto.ReceivableTypeResponse;

import java.util.List;

import com.srm.credit.mapper.ReceivableTypeMapper;
import com.srm.credit.service.ReceivableTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receivable-types")
public class ReceivableTypeController {

    private final ReceivableTypeService service;
    private final ReceivableTypeMapper mapper;

    public ReceivableTypeController(ReceivableTypeService service, ReceivableTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<ReceivableTypeResponse>> list() {
        return ResponseEntity.ok().body(service.list());
    }

    @PostMapping
    public ResponseEntity<ReceivableTypeResponse> create(@Valid @RequestBody ReceivableTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.create(request)));
    }
}
