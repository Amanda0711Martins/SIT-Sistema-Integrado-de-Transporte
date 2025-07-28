package com.logistica.operational.controller;

import com.logistica.operational.dto.CollectionOrderRequestDTO;
import com.logistica.operational.dto.CollectionOrderResponseDTO;
import com.logistica.operational.service.CollectionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collection-orders")
@RequiredArgsConstructor
public class CollectionOrderController {

    private final CollectionOrderService orderService;

    @PostMapping
    public ResponseEntity<CollectionOrderResponseDTO> createOrder(@RequestBody CollectionOrderRequestDTO request) {
        CollectionOrderResponseDTO createdOrder = orderService.createOrder(request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
}
