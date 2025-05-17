package com.logistics.operational.service;

import com.logistics.operational.domain.FreightCalculation;
import com.logistics.operational.dto.ClientDto;
import com.logistics.operational.dto.FreightCalculationRequest;
import com.logistics.operational.dto.FreightCalculationResponse;
import com.logistics.operational.exception.InvalidDataException;
import com.logistics.operational.repository.FreightCalculationRepository;
import com.logistics.operational.util.DistanceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FreightServiceTest {

    @Mock
    private FreightCalculationRepository freightCalculationRepository;

    @Mock
    private ClientServiceClient clientServiceClient;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private FreightServiceImpl freightService;

    private FreightCalculationRequest validRequest;
    private ClientDto clientDto;

    @BeforeEach
    void setUp() {
        validRequest = FreightCalculationRequest.builder()
                .clientId(1L)
                .origin("São Paulo")
                .destination("Rio de Janeiro")
                .weight(100.0)
                .volume(2.0)
                .rush(false)
                .fragile(false)
                .insurance(false)
                .build();

        clientDto = ClientDto.builder()
                .id(1L)
                .name("Test Client")
                .discountPercentage(10.0)
                .build();
    }

    @Test
    void shouldCalculateFreightSuccessfully() {
        // Arrange
        when(clientServiceClient.getClientById(1L)).thenReturn(clientDto);
        when(distanceCalculator.calculateDistance("São Paulo", "Rio de Janeiro")).thenReturn(400.0);

        FreightCalculation savedCalculation = FreightCalculation.builder()
                .id(1L)
                .clientId(1L)
                .origin("São Paulo")
                .destination("Rio de Janeiro")
                .distance(400.0)
                .weight(100.0)
                .volume(2.0)
                .baseValue(210.0)
                .totalValue(189.0) // After 10% discount
                .calculatedAt(LocalDateTime.now())
                .build();

        when(freightCalculationRepository.save(any())).thenReturn(savedCalculation);

        // Act
        FreightCalculationResponse response = freightService.calculateFreight(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(189.0, response.getTotalValue());
        verify(kafkaTemplate).send(eq("freight-calculations"), any(FreightCalculation.class));
    }

    @Test
    void shouldThrowExceptionWhenWeightIsZero() {
        // Arrange
        FreightCalculationRequest invalidRequest = FreightCalculationRequest.builder()
                .clientId(1L)
                .origin("São Paulo")
                .destination("Rio de Janeiro")
                .weight(0.0)
                .volume(2.0)
                .build();

        // Act & Assert
        assertThrows(InvalidDataException.class, () -> freightService.calculateFreight(invalidRequest));
        verify(freightCalculationRepository, never()).save(any());
    }

    @Test
    void shouldApplyRushFee() {
        // Arrange
        validRequest.setRush(true);
        when(clientServiceClient.getClientById(1L)).thenReturn(clientDto);
        when(distanceCalculator.calculateDistance("São Paulo", "Rio de Janeiro")).thenReturn(400.0);

        FreightCalculation savedCalculation = FreightCalculation.builder()
                .id(1L)
                .clientId(1L)
                .origin("São Paulo")
                .destination("Rio de Janeiro")
                .distance(400.0)
                .weight(100.0)
                .volume(2.0)
                .rush(true)
                .baseValue(210.0)
                .totalValue(236.25) // (210 * 1.25) * 0.9 (client discount)
                .calculatedAt(LocalDateTime.now())
                .build();

        when(freightCalculationRepository.save(any())).thenReturn(savedCalculation);

        // Act
        FreightCalculationResponse response = freightService.calculateFreight(validRequest);

        // Assert
        assertEquals(236.25, response.getTotalValue());
    }
}