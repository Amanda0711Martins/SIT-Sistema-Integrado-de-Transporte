package com.logistica.customer.controller;

import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.dto.CustomerPageResponseDTO;
import com.logistica.customer.model.Customer;
import com.logistica.customer.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        log.info("REST request to create a customer: {}", customerDTO);
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        log.info("REST request to get customer by id: {}", id);
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/cnpj")
    public ResponseEntity<CustomerDTO> getCustomerByCnpj(@RequestParam String cnpj) {
        log.info("REST request to get customer by CNPJ: {}", cnpj);
        CustomerDTO customer = customerService.getCustomerByCnpj(cnpj);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponseDTO> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.info("REST request to get all customers");
        CustomerPageResponseDTO customers = customerService.getAllCustomers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerPageResponseDTO> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Customer.CustomerStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search customers by name: {} and status: {}", name, status);
        CustomerPageResponseDTO customers = customerService.searchCustomers(name, status, page, size);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("REST request to update customer with id: {}", id);
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("REST request to delete customer with id: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerDTO> changeCustomerStatus(
            @PathVariable Long id,
            @RequestParam Customer.CustomerStatus status) {
        log.info("REST request to change status of customer with id: {} to {}", id, status);
        CustomerDTO updatedCustomer = customerService.changeCustomerStatus(id, status);
        return ResponseEntity.ok(updatedCustomer);
    }
}