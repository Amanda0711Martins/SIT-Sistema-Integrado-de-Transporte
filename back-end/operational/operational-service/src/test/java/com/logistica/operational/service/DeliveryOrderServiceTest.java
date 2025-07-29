package com.logistica.operational.service;

import com.logistica.operational.dto.FreightQuoteRequestDTO;
import com.logistica.operational.dto.FreightQuoteResponseDTO;
import com.logistica.operational.mapper.FreightQuoteMapper;
import com.logistica.operational.models.FreightQuote;
import com.logistica.operational.repository.FreightQuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreightQuoteServiceTest {

    @Mock
    private FreightQuoteRepository quoteRepository;

    @Mock
    private FreightQuoteMapper quoteMapper;

    @InjectMocks
    private FreightQuoteService freightQuoteService;

    private FreightQuoteRequestDTO requestDTO;
    private FreightQuote savedQuote;
    private FreightQuoteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Dados de entrada para o teste
        requestDTO = new FreightQuoteRequestDTO();
        requestDTO.setOriginCep("12345-000");
        requestDTO.setDestinationCep("54321-000");
        requestDTO.setWeightKg(10.0);

        // Objeto que simulamos ser salvo no banco
        savedQuote = new FreightQuote();
        savedQuote.setId(1L);
        savedQuote.setQuoteNumber("QT-12345678");
        savedQuote.setCalculatedValue(new BigDecimal("65.00"));

        // DTO que simulamos ser retornado pelo mapper
        responseDTO = new FreightQuoteResponseDTO();
        responseDTO.setQuoteNumber("QT-12345678");
        responseDTO.setCalculatedValue(new BigDecimal("65.00"));
    }

    @Test
    void testCalculateAndSaveQuote_Success() {
        // Arrange (Preparação dos Mocks)
        // Quando o repositório salvar qualquer objeto FreightQuote, retorne o nosso objeto simulado
        when(quoteRepository.save(any(FreightQuote.class))).thenReturn(savedQuote);
        // Quando o mapper converter a entidade salva, retorne o nosso DTO de resposta simulado
        when(quoteMapper.toResponseDto(any(FreightQuote.class))).thenReturn(responseDTO);

        // Act (Execução do método a ser testado)
        FreightQuoteResponseDTO result = freightQuoteService.calculateAndSaveQuote(requestDTO);

        // Assert (Verificação dos resultados e interações)
        assertNotNull(result);
        assertEquals("QT-12345678", result.getQuoteNumber());
        assertTrue(result.getCalculatedValue().compareTo(BigDecimal.ZERO) > 0);

        // Verifica se o método save do repositório foi chamado exatamente uma vez
        verify(quoteRepository, times(1)).save(any(FreightQuote.class));
        // Verifica se o método de conversão do mapper foi chamado exatamente uma vez
        verify(quoteMapper, times(1)).toResponseDto(savedQuote);
    }
}
