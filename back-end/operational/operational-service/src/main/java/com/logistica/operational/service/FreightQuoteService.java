// Arquivo: service/FreightQuoteService.java
// Arquivo: service/FreightQuoteService.java
package com.logistica.operational.service;

import com.logistica.operational.dto.FreightQuoteRequestDTO;
import com.logistica.operational.dto.FreightQuoteResponseDTO;
import com.logistica.operational.mapper.FreightQuoteMapper;
import com.logistica.operational.models.FreightQuote;
import com.logistica.operational.repository.FreightQuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreightQuoteService {

    private final FreightQuoteRepository quoteRepository;
    private final FreightQuoteMapper quoteMapper;

    public FreightQuoteResponseDTO calculateAndSaveQuote(FreightQuoteRequestDTO request) {
        // Lógica de cálculo simplificada
        BigDecimal baseRate = new BigDecimal("50.00");
        BigDecimal ratePerKg = new BigDecimal("1.50");
        BigDecimal weight = BigDecimal.valueOf(request.getWeightKg());
        
        BigDecimal calculatedValue = baseRate.add(ratePerKg.multiply(weight));

        FreightQuote quote = new FreightQuote();
        quote.setQuoteNumber("QT-" + UUID.randomUUID().toString().substring(0, 8));
        quote.setOriginCep(request.getOriginCep());
        quote.setDestinationCep(request.getDestinationCep());
        quote.setWeightKg(request.getWeightKg());
        quote.setCalculatedValue(calculatedValue);
        quote.setCreatedAt(LocalDateTime.now());
        
        FreightQuote savedQuote = quoteRepository.save(quote);
        
        return quoteMapper.toResponseDto(savedQuote);
    }
}

// Arquivo: service/CollectionOrderService.java
