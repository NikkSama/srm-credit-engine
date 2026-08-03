package com.srm.credit.mapper;

import com.srm.credit.domain.ReceivableType;
import com.srm.credit.dto.ReceivableTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReceivableTypeMapper {

    ReceivableTypeResponse toResponse(ReceivableType type);
}
