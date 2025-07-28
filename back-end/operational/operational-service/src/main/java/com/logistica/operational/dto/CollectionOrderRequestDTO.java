package com.logistica.operational.dto;

import lombok.Data;

@Data
public class CollectionOrderRequestDTO {
    private Long customerId;
    private String originAddress;
    private String destinationAddress;
    private String goodsDescription;
    private Long quoteId; // Opcional
}