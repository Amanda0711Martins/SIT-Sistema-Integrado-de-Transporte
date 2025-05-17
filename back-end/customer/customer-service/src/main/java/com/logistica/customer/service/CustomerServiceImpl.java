package com.logistica.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.dto.CustomerPageResponseDTO;
import com.logistica.customer.exception.CustomerNotFoundException;
import com.logistica.customer.exception.ValidationException;
import com.logistica.customer.model.Customer;
import com.logistica.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating a new customer: {}", customerDTO.getName());

        // Verificar se já existe cliente com o mesmo CNPJ
        Optional<Customer> existingByCnpj = customerRepository.findByCnpj(customerDTO.getCnpj());
        if (existingByCnpj.isPresent()) {
            throw new ValidationException("Já existe um cliente cadastrado com este CNPJ");
        }

        // Verificar se já existe cliente com o mesmo email
        Optional<Customer> existingByEmail = customerRepository.findByEmail(customerDTO.getEmail());
        if (existingByEmail.isPresent()) {
            throw new ValidationException("Já existe um cliente cadastrado com este e-mail");
        }

        Customer customerToSave = customerDTO.toEntity();
        Customer savedCustomer = customerRepository.save(customerToSave);

        // Registrar a criação na auditoria
        auditService.logEvent("Customer", savedCustomer.getId(), "CREATE", null,
                toJsonString(savedCustomer));

        return CustomerDTO.fromEntity(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        log.info("Fetching customer by id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado com id: " + id));
        return CustomerDTO.fromEntity(customer);
    }

    @Override
    public CustomerDTO getCustomerByCnpj(String cnpj) {
        log.info("Fetching customer by CNPJ: {}", cnpj);
        Customer customer = customerRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado com CNPJ: " + cnpj));
        return CustomerDTO.fromEntity(customer);
    }

    @Override
    public CustomerPageResponseDTO getAllCustomers(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all customers, page: {}, size: {}", page, size);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return mapToPageResponse(customerPage);
    }

    @Override
    public CustomerPageResponseDTO searchCustomers(String name, Customer.CustomerStatus status, int page, int size) {
        log.info("Searching customers by name: {} and status: {}, page: {}, size: {}", name, status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage;

        if (name != null && !name.isEmpty() && status != null) {
            customerPage = customerRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        } else if (name != null && !name.isEmpty()) {
            customerPage = customerRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (status != null) {
            customerPage = customerRepository.findByStatus(status, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        return mapToPageResponse(customerPage);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer with id: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado com id: " + id));

        // Verificar se o CNPJ está sendo alterado e se já existe outro cliente com o novo CNPJ
        if (!existingCustomer.getCnpj().equals(customerDTO.getCnpj())) {
            Optional<Customer> existingByCnpj = customerRepository.findByCnpj(customerDTO.getCnpj());
            if (existingByCnpj.isPresent() && !existingByCnpj.get().getId().equals(id)) {
                throw new ValidationException("Já existe um cliente cadastrado com este CNPJ");
            }
        }

        // Verificar se o e-mail está sendo alterado e se já existe outro cliente com o novo e-mail
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail())) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(customerDTO.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                throw new ValidationException("Já existe um cliente cadastrado com este e-mail");
            }
        }

        String oldValue = toJsonString(existingCustomer);

        // Atualizar os dados do cliente
        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setCnpj(customerDTO.getCnpj());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setCity(customerDTO.getCity());
        existingCustomer.setState(customerDTO.getState());
        existingCustomer.setZipCode(customerDTO.getZipCode());
        existingCustomer.setNotes(customerDTO.getNotes());
        if (customerDTO.getStatus() != null) {
            existingCustomer.setStatus(customerDTO.getStatus());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        // Registrar a atualização na auditoria
        auditService.logEvent("Customer", updatedCustomer.getId(), "UPDATE", oldValue,
                toJsonString(updatedCustomer));

        return CustomerDTO.fromEntity(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado com id: " + id));

        String oldValue = toJsonString(customer);

        customerRepository.delete(customer);

        // Registrar a deleção na auditoria
        auditService.logEvent("Customer", id, "DELETE", oldValue, null);
    }

    @Override
    @Transactional
    public CustomerDTO changeCustomerStatus(Long id, Customer.CustomerStatus status) {
        log.info("Changing status of customer with id: {} to {}", id, status);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado com id: " + id));

        String oldValue = toJsonString(customer);

        customer.setStatus(status);
        Customer updatedCustomer = customerRepository.save(customer);

        // Registrar a alteração de status na auditoria
        auditService.logEvent("Customer", customer.getId(), "STATUS_CHANGE", oldValue,
                toJsonString(updatedCustomer));

        return CustomerDTO.fromEntity(updatedCustomer);
    }

    private CustomerPageResponseDTO mapToPageResponse(Page<Customer> page) {
        return new CustomerPageResponseDTO(
                page.getContent().stream()
                        .map(CustomerDTO::fromEntity)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
            return "{}";
        }
    }
}