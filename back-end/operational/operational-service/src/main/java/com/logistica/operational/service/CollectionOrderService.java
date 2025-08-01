package com.logistica.operational.service;
import java.time.LocalDateTime;
import java.util.UUID;

import com.logistica.operational.mapper.CollectionOrderMapper;
import com.logistica.operational.repository.CollectionOrderRepository;
import org.springframework.stereotype.Service;

import com.logistica.operational.client.CustomerServiceClient;
import com.logistica.operational.dto.CollectionOrderRequestDTO;
import com.logistica.operational.dto.CollectionOrderResponseDTO;
import com.logistica.operational.dto.CustomerDTO;
import com.logistica.operational.models.CollectionOrder;

@Service
public class CollectionOrderService {
    
    private final CollectionOrderRepository orderRepository;
    private final CollectionOrderMapper orderMapper;
    private final CustomerServiceClient customerServiceClient;

    public CollectionOrderService(CollectionOrderRepository orderRepository, CollectionOrderMapper orderMapper, CustomerServiceClient customerServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerServiceClient = customerServiceClient;
    }


    public CollectionOrderResponseDTO createOrder(CollectionOrderRequestDTO request) {
        CustomerDTO customer = customerServiceClient.getCustomerById(request.getCustomerId());

        CollectionOrder order = orderMapper.toEntity(request);
        order.setOrderNumber("CO-" + UUID.randomUUID().toString().substring(0, 8));
        order.setStatus(CollectionOrder.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        CollectionOrder savedOrder = orderRepository.save(order);

        CollectionOrderResponseDTO responseDTO = orderMapper.toResponseDto(savedOrder);
        
        // 5. Enriquecer a resposta com o nome do cliente
        responseDTO.setCustomerName(customer.getName());

        return responseDTO;
    }
}
