package com.logistics.operational.controller;

import com.logistics.operational.dto.VehicleRequest;
import com.logistics.operational.dto.VehicleResponse;
import com.logistics.operational.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLEET_MANAGER')")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        log.info("Creating new vehicle with license plate: {}", request.getLicensePlate());
        return new ResponseEntity<>(vehicleService.createVehicle(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long id) {
        log.info("Fetching vehicle with id: {}", id);
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<VehicleResponse>> getAllVehicles(
            Pageable pageable,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        log.info("Fetching all vehicles with filters - type: {}, status: {}", type, status);
        return ResponseEntity.ok(vehicleService.getAllVehicles(pageable, type, status));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLEET_MANAGER')")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        log.info("Updating vehicle with id: {}", id);
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.info("Deleting vehicle with id: {}", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLEET_MANAGER')")
    public ResponseEntity<VehicleResponse> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Updating status of vehicle with id: {} to status: {}", id, status);
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }
}