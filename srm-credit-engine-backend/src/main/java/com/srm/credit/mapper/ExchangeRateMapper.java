package com.srm.credit.mapper;

import com.srm.credit.domain.ExchangeRate;
import com.srm.credit.dto.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {

    @Mapping(target = "baseCurrency", source = "baseCurrency.code")
    @Mapping(target = "quoteCurrency", source = "quoteCurrency.code")
    ExchangeRateResponse toResponse(ExchangeRate rate);
}