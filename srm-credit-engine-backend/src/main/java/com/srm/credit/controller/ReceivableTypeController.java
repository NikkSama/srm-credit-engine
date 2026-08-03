package com.srm.credit.controller;

import com.srm.credit.dto.ReceivableTypeResponse;
import com.srm.credit.repository.ReceivableTypeRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/receivable-types")
public class ReceivableTypeController {

    private final ReceivableTypeRepository repository;

    public ReceivableTypeController(ReceivableTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ReceivableTypeResponse> list() {
        return repository.findAll().stream()
                .map(type -> new ReceivableTypeResponse(
                        type.getId(), type.getName(), type.getMonthlySpread()))
                .toList();
    }
}
