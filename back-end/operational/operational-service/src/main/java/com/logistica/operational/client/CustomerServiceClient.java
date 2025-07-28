package com.logistica.operational.client;

import com.logistica.operational.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service") // Nome do microserviço no Eureka
public interface CustomerServiceClient {

    // O endpoint deve corresponder ao que está exposto no CustomerController
    @GetMapping("/api/v1/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") Long id);
}
