package com.logistics.operational.service;

import com.logistics.operational.domain.FreightCalculation;
import com.logistics.operational.dto.FreightCalculationRequest;
import com.logistics.operational.dto.FreightCalculationResponse;

import java.util.List;

public interface FreightService {

    FreightCalculationResponse calculateFreight(FreightCalculationRequest request);

    List<FreightCalculationResponse> getFreightCalculationHistory(Long clientId);

    FreightCalculationResponse getFreightCalculationById(Long id);

    void deleteFreightCalculation(Long id);
}