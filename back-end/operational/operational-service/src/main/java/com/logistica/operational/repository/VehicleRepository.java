package com.logistica.operational.repository;

import com.logistica.operational.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByTypeAndStatus(Vehicle.VehicleType type, Vehicle.VehicleStatus status);
}