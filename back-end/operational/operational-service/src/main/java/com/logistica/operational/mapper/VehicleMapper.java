package com.logistica.operational.mapper;

import com.logistica.operational.dto.VehicleDTO;
import com.logistica.operational.models.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleDTO toDto(Vehicle vehicle);
    Vehicle toEntity(VehicleDTO dto);
}