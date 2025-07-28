package com.logistica.operational.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "collection_orders")
@Data
public class CollectionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String originAddress;

    @Column(nullable = false)
    private String destinationAddress;

    private String goodsDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private Long assignedVehicleId;

    private Long quoteId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;

    public enum OrderStatus { PENDING, SCHEDULED, IN_TRANSIT, DELIVERED, CANCELED }
}
