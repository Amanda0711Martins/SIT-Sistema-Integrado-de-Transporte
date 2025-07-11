package com.logistics.financial.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.logistics.financial.dto.ClientDTO;

@FeignClient(name = "client-service")
public interface ClientServiceClient {

    @GetMapping("/api/clients/{id}")
    ClientDTO getClientById(@PathVariable("id") Long id);
}