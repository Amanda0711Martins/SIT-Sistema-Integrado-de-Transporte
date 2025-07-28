package com.logistica.operational.mapper;

import com.logistica.operational.dto.FreightQuoteResponseDTO;
import com.logistica.operational.models.FreightQuote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FreightQuoteMapper {
    FreightQuoteResponseDTO toResponseDto(FreightQuote freightQuote);
}