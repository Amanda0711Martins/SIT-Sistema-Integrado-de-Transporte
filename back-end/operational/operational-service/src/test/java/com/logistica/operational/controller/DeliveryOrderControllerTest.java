package com.logistica.operational.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.operational.dto.FreightQuoteRequestDTO;
import com.logistica.operational.dto.FreightQuoteResponseDTO;
import com.logistica.operational.service.FreightQuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FreightQuoteController.class) // Testa apenas a camada web para este controller
class FreightQuoteControllerTest {

    @Autowired
    private MockMvc mockMvc; // Ferramenta para simular requisições HTTP

    @MockBean
    private FreightQuoteService freightQuoteService; // Mock da camada de serviço

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos em JSON

    private FreightQuoteRequestDTO requestDTO;
    private FreightQuoteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new FreightQuoteRequestDTO();
        requestDTO.setOriginCep("12345-000");
        requestDTO.setDestinationCep("54321-000");
        requestDTO.setWeightKg(25.5);

        responseDTO = new FreightQuoteResponseDTO();
        responseDTO.setQuoteNumber("QT-TEST-123");
        responseDTO.setCalculatedValue(new BigDecimal("88.25"));
    }

    @Test
    void testCalculateQuote_shouldReturn200_whenRequestIsValid() throws Exception {
        // Arrange
        // Simula o comportamento do serviço: quando chamado, retorna o nosso DTO de resposta
        when(freightQuoteService.calculateAndSaveQuote(any(FreightQuoteRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        // Executa uma requisição POST para "/api/v1/quotes/calculate"
        mockMvc.perform(post("/api/v1/quotes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))) // Converte o DTO de requisição para JSON
                .andExpect(status().isOk()) // Verifica se o status da resposta é 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verifica se o tipo de conteúdo é JSON
                .andExpect(jsonPath("$.quoteNumber").value("QT-TEST-123")) // Verifica o campo 'quoteNumber' na resposta
                .andExpect(jsonPath("$.calculatedValue").value(88.25)); // Verifica o campo 'calculatedValue'
    }
}
