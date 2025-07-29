package com.logistica.operational.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.operational.dto.CollectionOrderRequestDTO;
import com.logistica.operational.dto.CollectionOrderResponseDTO;
import com.logistica.operational.models.CollectionOrder;
import com.logistica.operational.service.CollectionOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CollectionOrderController.class)
class CollectionOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollectionOrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private CollectionOrderRequestDTO requestDTO;
    private CollectionOrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CollectionOrderRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setOriginAddress("Origem Teste");
        requestDTO.setDestinationAddress("Destino Teste");

        responseDTO = new CollectionOrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOrderNumber("CO-TESTE-987");
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Cliente Mock");
        responseDTO.setStatus(CollectionOrder.OrderStatus.PENDING);
        responseDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateOrder_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        // Simula o comportamento do serviço de criação de pedido
        when(orderService.createOrder(any(CollectionOrderRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/collection-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Verifica se o status da resposta é 201 (Created)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderNumber").value("CO-TESTE-987"))
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.customerName").value("Cliente Mock"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
