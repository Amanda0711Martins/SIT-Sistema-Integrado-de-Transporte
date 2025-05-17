package com.logistics.operational.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}", message = "Invalid license plate format")
    @Column(unique = true)
    private String licensePlate;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive number")
    private Double capacity; // in tons

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private String currentDriver;

    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.licensePlate = this.licensePlate.toUpperCase();
    }

    public enum VehicleStatus {
        ACTIVE, MAINTENANCE, INACTIVE
    }
}