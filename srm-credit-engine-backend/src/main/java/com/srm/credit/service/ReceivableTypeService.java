package com.srm.credit.service;

import com.srm.credit.domain.ReceivableType;
import com.srm.credit.dto.ReceivableTypeRequest;
import com.srm.credit.dto.ReceivableTypeResponse;
import com.srm.credit.exception.BusinessException;
import com.srm.credit.mapper.ReceivableTypeMapper;
import com.srm.credit.repository.ReceivableTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReceivableTypeService {

    private final ReceivableTypeRepository repository;
    private final ReceivableTypeMapper mapper;

    public ReceivableTypeService(ReceivableTypeRepository repository, ReceivableTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ReceivableTypeResponse> list() {
        log.debug("Listing all receivable types");
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public ReceivableType create(ReceivableTypeRequest request) {
        log.info("Attempting to create new receivable type with name: {}", request.name());

        repository.findByName(request.name()).ifPresent(existing -> {
            log.warn("Failed to create receivable type. Name already exists: {}", request.name());
            throw new BusinessException("ReceivableType with name " + request.name() + " already exists.");
        });

        ReceivableType entity = mapper.toEntity(request);
        ReceivableType saved = repository.save(entity);

        log.info("Receivable type created successfully. ID: {}, Name: {}", saved.getId(), saved.getName());
        return saved;
    }
}