package com.logistica.operational.dto;

import com.logistica.operational.models.Vehicle;
import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private String model;
    private String brand;
    private Double capacityKg;
    private Vehicle.VehicleType type;
    private Vehicle.VehicleStatus status;
}