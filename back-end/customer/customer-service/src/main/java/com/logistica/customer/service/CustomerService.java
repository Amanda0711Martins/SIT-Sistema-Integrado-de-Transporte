package com.logistica.customer.service;

import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.dto.CustomerPageResponseDTO;
import com.logistica.customer.model.Customer;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO getCustomerById(Long id);

    CustomerDTO getCustomerByCnpj(String cnpj);

    CustomerPageResponseDTO getAllCustomers(int page, int size, String sortBy, String sortDirection);

    CustomerPageResponseDTO searchCustomers(String name, Customer.CustomerStatus status, int page, int size);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);

    CustomerDTO changeCustomerStatus(Long id, Customer.CustomerStatus status);
}