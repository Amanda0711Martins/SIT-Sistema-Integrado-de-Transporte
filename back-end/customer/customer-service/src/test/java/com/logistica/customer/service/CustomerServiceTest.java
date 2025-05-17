package com.logistica.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.exception.CustomerNotFoundException;
import com.logistica.customer.exception.ValidationException;
import com.logistica.customer.model.Customer;
import com.logistica.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Empresa ABC");
        customer.setCnpj("12.345.678/0001-90");
        customer.setEmail("contato@empresaabc.com.br");
        customer.setPhone("(11) 5555-5555");
        customer.setAddress("Rua Exemplo, 123");
        customer.setCity("São Paulo");
        customer.setState("SP");
        customer.setZipCode("01234-567");
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        customerDTO = CustomerDTO.fromEntity(customer);
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(customerRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getCnpj(), result.getCnpj());

        verify(customerRepository).save(any(Customer.class));
        verify(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCustomerWithExistingCnpj() {
        when(customerRepository.findByCnpj(anyString())).thenReturn(Optional.of(customer));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerService.createCustomer(customerDTO);
        });

        assertEquals("Já existe um cliente cadastrado com este CNPJ", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCustomerByIdSuccessfully() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getCnpj(), result.getCnpj());

        verify(customerRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());

        customerDTO.setName("Empresa XYZ Atualizada");
        CustomerDTO result = customerService.updateCustomer(1L, customerDTO);

        assertNotNull(result);
        assertEquals("Empresa XYZ Atualizada", result.getName());

        verify(customerRepository).save(any(Customer.class));
        verify(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(any(Customer.class));
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(customer);
        verify(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldGetAllCustomersSuccessfully() {
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = customerService.getAllCustomers(0, 10, "name", "ASC");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());

        verify(customerRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldSearchCustomersByNameAndStatusSuccessfully() {
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.findByNameContainingIgnoreCaseAndStatus(anyString(), any(Customer.CustomerStatus.class), any(Pageable.class)))
                .thenReturn(page);

        var result = customerService.searchCustomers("Empresa", Customer.CustomerStatus.ACTIVE, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(customerRepository).findByNameContainingIgnoreCaseAndStatus(anyString(), any(Customer.CustomerStatus.class), any(Pageable.class));
    }

    @Test
    void shouldChangeCustomerStatusSuccessfully() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());

        CustomerDTO result = customerService.changeCustomerStatus(1L, Customer.CustomerStatus.INACTIVE);

        assertNotNull(result);
        assertEquals(Customer.CustomerStatus.INACTIVE, result.getStatus());

        verify(customerRepository).save(any(Customer.class));
        verify(auditService).logEvent(anyString(), anyLong(), anyString(), anyString(), anyString());
    }
}