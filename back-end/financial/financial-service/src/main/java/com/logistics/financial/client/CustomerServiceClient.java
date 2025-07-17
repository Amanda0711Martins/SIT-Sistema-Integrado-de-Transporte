package com.logistics.financial.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.logistics.financial.dto.ClientDTO;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    
    @GetMapping("/api/v1/customers/{id}")
    ClientDTO getClientById(@PathVariable("id") Long id);
}
