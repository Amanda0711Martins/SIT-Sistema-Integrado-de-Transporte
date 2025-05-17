package com.logistics.operational.service;

import com.logistics.operational.dto.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service", fallbackFactory = ClientServiceFallbackFactory.class)
public interface ClientServiceClient {

    @GetMapping("/api/clients/{id}")
    ClientDto getClientById(@PathVariable("id") Long id);
}