package com.logistica.operational.mapper;

import com.logistica.operational.dto.CollectionOrderRequestDTO;
import com.logistica.operational.dto.CollectionOrderResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollectionOrderMapper {
    com.logistica.operational.models.CollectionOrder toEntity(CollectionOrderRequestDTO requestDTO);
    
    @Mapping(target = "customerName", ignore = true) // Ignora, pois será preenchido manualmente
    CollectionOrderResponseDTO toResponseDto(com.logistica.operational.models.CollectionOrder order);
}