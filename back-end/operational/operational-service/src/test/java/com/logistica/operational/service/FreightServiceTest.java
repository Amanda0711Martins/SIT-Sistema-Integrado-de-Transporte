package com.logistica.operational.service;

import com.logistica.operational.client.CustomerServiceClient;
import com.logistica.operational.dto.CollectionOrderRequestDTO;
import com.logistica.operational.dto.CollectionOrderResponseDTO;
import com.logistica.operational.dto.CustomerDTO;
import com.logistica.operational.mapper.CollectionOrderMapper;
import com.logistica.operational.models.CollectionOrder;
import com.logistica.operational.repository.CollectionOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionOrderServiceTest {

    @Mock
    private CollectionOrderRepository orderRepository;

    @Mock
    private CollectionOrderMapper orderMapper;

    @Mock
    private CustomerServiceClient customerServiceClient; // Mock para o cliente Feign

    @InjectMocks
    private CollectionOrderService collectionOrderService;

    private CollectionOrderRequestDTO requestDTO;
    private CustomerDTO customerDTO;
    private CollectionOrder orderToSave;
    private CollectionOrder savedOrder;
    private CollectionOrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // DTO de requisição
        requestDTO = new CollectionOrderRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setOriginAddress("Rua A, 123");
        requestDTO.setDestinationAddress("Rua B, 456");

        // DTO do cliente que viria do microserviço externo
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("Cliente Teste");

        // Entidade que o mapper converte
        orderToSave = new CollectionOrder();
        orderToSave.setCustomerId(1L);

        // Entidade que o repositório retorna
        savedOrder = new CollectionOrder();
        savedOrder.setId(10L);
        savedOrder.setCustomerId(1L);
        savedOrder.setOrderNumber("CO-123");
        savedOrder.setStatus(CollectionOrder.OrderStatus.PENDING);

        // DTO de resposta que o mapper converte
        responseDTO = new CollectionOrderResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setCustomerId(1L);
        responseDTO.setOrderNumber("CO-123");
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        // Simula a chamada bem-sucedida ao microserviço de cliente
        when(customerServiceClient.getCustomerById(1L)).thenReturn(customerDTO);
        // Simula as conversões do mapper
        when(orderMapper.toEntity(any(CollectionOrderRequestDTO.class))).thenReturn(orderToSave);
        when(orderMapper.toResponseDto(any(CollectionOrder.class))).thenReturn(responseDTO);
        // Simula o salvamento no repositório
        when(orderRepository.save(any(CollectionOrder.class))).thenReturn(savedOrder);

        // Act
        CollectionOrderResponseDTO result = collectionOrderService.createOrder(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Cliente Teste", result.getCustomerName()); // Valida o enriquecimento com o nome do cliente
        assertEquals(CollectionOrder.OrderStatus.PENDING, orderToSave.getStatus()); // Valida a regra de negócio
        assertNotNull(orderToSave.getOrderNumber()); // Valida se o número do pedido foi gerado

        // Verifica as interações com os mocks
        verify(customerServiceClient).getCustomerById(1L);
        verify(orderRepository).save(orderToSave);
        verify(orderMapper).toResponseDto(savedOrder);
    }

    @Test
    void testCreateOrder_FailsWhenCustomerNotFound() {
        // Arrange
        // Simula uma falha na chamada ao microserviço de cliente
        when(customerServiceClient.getCustomerById(anyLong())).thenThrow(new RuntimeException("Cliente não encontrado"));

        // Act & Assert
        // Verifica se a exceção do cliente Feign é propagada
        assertThrows(RuntimeException.class, () -> {
            collectionOrderService.createOrder(requestDTO);
        });

        // Garante que, se o cliente não existe, nenhum pedido é salvo
        verify(orderRepository, never()).save(any(CollectionOrder.class));
    }
}
