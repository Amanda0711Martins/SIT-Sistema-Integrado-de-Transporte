package com.logistics.financial.client;

import com.logistics.financial.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface ClientServiceClient {

    @GetMapping("/api/clients/{id}")
    ClientDTO getClientById(@PathVariable("id") Long id);
}