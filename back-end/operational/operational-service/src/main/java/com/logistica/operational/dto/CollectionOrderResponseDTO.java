package com.logistica.operational.dto;

import com.logistica.operational.models.CollectionOrder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectionOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName; // Será preenchido via Feign
    private String originAddress;
    private String destinationAddress;
    private CollectionOrder.OrderStatus status;
    private LocalDateTime createdAt;
}