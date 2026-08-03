package com.srm.credit.service;

import com.srm.credit.domain.ReceivableType;
import com.srm.credit.dto.ReceivableTypeRequest;
import com.srm.credit.dto.ReceivableTypeResponse;
import com.srm.credit.mapper.ReceivableTypeMapper;
import com.srm.credit.repository.ReceivableTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceivableTypeService {

    ReceivableTypeRepository repository;
    ReceivableTypeMapper mapper;

    public ReceivableTypeService(ReceivableTypeRepository repository, ReceivableTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ReceivableTypeResponse> list() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public ReceivableType create(ReceivableTypeRequest request) {
        ReceivableType entity = mapper.toEntity(request);
        return repository.save(entity);
    }
}
