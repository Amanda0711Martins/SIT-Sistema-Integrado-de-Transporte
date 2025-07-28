package com.logistica.operational.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_quotes")
@Data
public class FreightQuote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String quoteNumber;

    @Column(nullable = false)
    private String originCep;

    @Column(nullable = false)
    private String destinationCep;
    
    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private BigDecimal calculatedValue;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}