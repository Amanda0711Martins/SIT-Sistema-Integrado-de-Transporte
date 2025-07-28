// Arquivo: model/Vehicle.java
package com.logistica.operational.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Double capacityKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    public enum VehicleType { TRUCK, VAN, MOTORCYCLE }
    public enum VehicleStatus { AVAILABLE, IN_TRANSIT, MAINTENANCE }
}
