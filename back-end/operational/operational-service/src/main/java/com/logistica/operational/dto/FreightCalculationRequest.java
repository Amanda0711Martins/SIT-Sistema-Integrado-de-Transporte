package com.logistica.operational.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightCalculationRequest {

    @NotNull(message = "Cliente ID é obrigatório")
    private Long clientId;

    @NotBlank(message = "Origem é obrigatória")
    private String origin;

    @NotBlank(message = "Destino é obrigatório")
    private String destination;

    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser um valor positivo")
    private Double weight;

    @NotNull(message = "Volume é obrigatório")
    @Positive(message = "Volume deve ser um valor positivo")
    private Double volume;

    private String productType;

    private Boolean rush;

    private Boolean fragile;

    private Boolean insurance;
}