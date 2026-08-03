package com.srm.credit.controller;

import com.srm.credit.dto.ReceivableTypeResponse;
import com.srm.credit.mapper.ReceivableTypeMapper;
import com.srm.credit.repository.ReceivableTypeRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/receivable-types")
public class ReceivableTypeController {

    private final ReceivableTypeRepository repository;
    private final ReceivableTypeMapper mapper;

    public ReceivableTypeController(ReceivableTypeRepository repository, ReceivableTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ReceivableTypeResponse> list() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }
}
