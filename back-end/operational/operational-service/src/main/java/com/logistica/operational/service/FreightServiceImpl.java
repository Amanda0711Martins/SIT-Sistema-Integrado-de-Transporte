package com.logistics.operational.service;

import com.logistics.operational.domain.FreightCalculation;
import com.logistics.operational.dto.ClientDto;
import com.logistics.operational.dto.FreightCalculationRequest;
import com.logistics.operational.dto.FreightCalculationResponse;
import com.logistics.operational.exception.InvalidDataException;
import com.logistics.operational.exception.ResourceNotFoundException;
import com.logistics.operational.repository.FreightCalculationRepository;
import com.logistics.operational.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreightServiceImpl implements FreightService {

    private final FreightCalculationRepository freightCalculationRepository;
    private final ClientServiceClient clientServiceClient;
    private final DistanceCalculator distanceCalculator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Base rate per km
    private static final double BASE_RATE_PER_KM = 0.5;
    // Rate per kg
    private static final double RATE_PER_KG = 0.1;

    @Override
    @Transactional
    public FreightCalculationResponse calculateFreight(FreightCalculationRequest request) {
        validateRequest(request);

        // Get client data to apply specific rates/discounts
        ClientDto client = clientServiceClient.getClientById(request.getClientId());

        // Calculate distance between origin and destination
        double distance = distanceCalculator.calculateDistance(request.getOrigin(), request.getDestination());

        // Calculate base freight value
        double baseFreight = distance * BASE_RATE_PER_KM + request.getWeight() * RATE_PER_KG;

        // Apply multipliers based on options
        double totalValue = calculateTotalValue(baseFreight, request, client);

        // Save calculation to database
        FreightCalculation freightCalculation = FreightCalculation.builder()
                .clientId(request.getClientId())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .distance(distance)
                .weight(request.getWeight())
                .volume(request.getVolume())
                .productType(request.getProductType())
                .rush(request.getRush())
                .fragile(request.getFragile())
                .insurance(request.getInsurance())
                .baseValue(baseFreight)
                .totalValue(totalValue)
                .calculatedAt(LocalDateTime.now())
                .build();

        FreightCalculation savedCalculation = freightCalculationRepository.save(freightCalculation);

        // Send event to notify about new freight calculation
        kafkaTemplate.send("freight-calculations", savedCalculation);

        log.info("Freight calculation completed for clientId: {} with value: {}", request.getClientId(), totalValue);

        return mapToResponse(savedCalculation);
    }

    @Override
    public List<FreightCalculationResponse> getFreightCalculationHistory(Long clientId) {
        List<FreightCalculation> calculations = freightCalculationRepository.findByClientIdOrderByCalculatedAtDesc(clientId);
        return calculations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FreightCalculationResponse getFreightCalculationById(Long id) {
        FreightCalculation calculation = freightCalculationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Freight calculation not found with id " + id));
        return mapToResponse(calculation);
    }

    @Override
    @Transactional
    public void deleteFreightCalculation(Long id) {
        if (!freightCalculationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Freight calculation not found with id " + id);
        }
        freightCalculationRepository.deleteById(id);
    }

    private void validateRequest(FreightCalculationRequest request) {
        if (request.getWeight() <= 0) {
            throw new InvalidDataException("Weight must be greater than zero");
        }
        if (request.getVolume() <= 0) {
            throw new InvalidDataException("Volume must be greater than zero");
        }
    }

    private double calculateTotalValue(double baseFreight, FreightCalculationRequest request, ClientDto client) {
        double totalValue = baseFreight;

        // Apply rush fee (25% extra)
        if (Boolean.TRUE.equals(request.getRush())) {
            totalValue *= 1.25;
        }

        // Apply fragile fee (15% extra)
        if (Boolean.TRUE.equals(request.getFragile())) {
            totalValue *= 1.15;
        }

        // Apply insurance fee (10% extra)
        if (Boolean.TRUE.equals(request.getInsurance())) {
            totalValue *= 1.10;
        }

        // Apply client discount if applicable
        if (client.getDiscountPercentage() != null && client.getDiscountPercentage() > 0) {
            double discountMultiplier = 1 - (client.getDiscountPercentage() / 100.0);
            totalValue *= discountMultiplier;
        }

        // Round to 2 decimal places
        BigDecimal bd = new BigDecimal(totalValue).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private FreightCalculationResponse mapToResponse(FreightCalculation calculation) {
        return FreightCalculationResponse.builder()
                .id(calculation.getId())
                .clientId(calculation.getClientId())
                .origin(calculation.getOrigin())
                .destination(calculation.getDestination())
                .distance(calculation.getDistance())
                .weight(calculation.getWeight())
                .volume(calculation.getVolume())
                .productType(calculation.getProductType())
                .rush(calculation.getRush())
                .fragile(calculation.getFragile())
                .insurance(calculation.getInsurance())
                .baseValue(calculation.getBaseValue())
                .totalValue(calculation.getTotalValue())
                .calculatedAt(calculation.getCalculatedAt())
                .build();
    }
}