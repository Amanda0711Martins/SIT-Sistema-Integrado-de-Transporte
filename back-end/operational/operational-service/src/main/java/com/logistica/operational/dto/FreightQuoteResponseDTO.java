package com.logistica.operational.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FreightQuoteResponseDTO {
    private String quoteNumber;
    private BigDecimal calculatedValue;
}