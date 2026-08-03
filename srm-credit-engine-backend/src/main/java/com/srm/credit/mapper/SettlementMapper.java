package com.srm.credit.mapper;

import com.srm.credit.domain.Settlement;
import com.srm.credit.dto.SettlementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettlementMapper {

    @Mapping(target = "receivableType", source = "receivableType.name")
    @Mapping(target = "originalCurrency", source = "originalCurrency.code")
    @Mapping(target = "paymentCurrency", source = "paymentCurrency.code")
    @Mapping(target = "exchangeRate", source = "exchangeRate.rate")
    @Mapping(target = "crossCurrency", expression =
            "java(!entity.getOriginalCurrency().getCode()"
                    + ".equalsIgnoreCase(entity.getPaymentCurrency().getCode()))")
    SettlementResponse toResponse(Settlement entity);
}
