package com.logistica.operational.dto;

import lombok.Data;

@Data
public class FreightQuoteRequestDTO {
    private String originCep;
    private String destinationCep;
    private Double weightKg;
}