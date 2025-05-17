package com.logistica.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.dto.CustomerPageResponseDTO;
import com.logistica.customer.exception.CustomerNotFoundException;
import com.logistica.customer.exception.ValidationException;
import com.logistica.customer.model.Customer;
import com.logistica.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO customerDTO;
    private CustomerPageResponseDTO pageResponseDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("Empresa ABC");
        customerDTO.setCnpj("12.345.678/0001-90");
        customerDTO.setEmail("contato@empresaabc.com.br");
        customerDTO.setPhone("(11) 5555-5555");
        customerDTO.setAddress("Rua Exemplo, 123");
        customerDTO.setCity("São Paulo");
        customerDTO.setState("SP");
        customerDTO.setZipCode("01234-567");
        customerDTO.setStatus(Customer.CustomerStatus.ACTIVE);

        List<CustomerDTO> customers = new ArrayList<>();
        customers.add(customerDTO);

        pageResponseDTO = new CustomerPageResponseDTO(
                customers,
                0,
                10,
                1L,
                1,
                true
        );
    }

    @Test
    @WithMockUser
    void shouldCreateCustomerSuccessfully() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Empresa ABC")));

        verify(customerService).createCustomer(any(CustomerDTO.class));
    }

    @Test
    @WithMockUser
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenReturn(customerDTO);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Empresa ABC")));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @WithMockUser
    void shouldHandleCustomerNotFoundException() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenThrow(new CustomerNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @WithMockUser
    void shouldGetCustomerByCnpjSuccessfully() throws Exception {
        when(customerService.getCustomerByCnpj(anyString())).thenReturn(customerDTO);

        mockMvc.perform(get("/api/v1/customers/cnpj/12.345.678/0001-90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Empresa ABC")));

        verify(customerService).getCustomerByCnpj("12.345.678/0001-90");
    }

    @Test
    @WithMockUser
    void shouldGetAllCustomersSuccessfully() throws Exception {
        when(customerService.getAllCustomers(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageResponseDTO);

        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Empresa ABC")));

        verify(customerService).getAllCustomers(0, 10, "name", "ASC");
    }

    @Test
    @WithMockUser
    void shouldSearchCustomersSuccessfully() throws Exception {
        when(customerService.searchCustomers(anyString(), any(Customer.CustomerStatus.class), anyInt(), anyInt())).thenReturn(pageResponseDTO);

        mockMvc.perform(get("/api/v1/customers/search")
                        .param("name", "Empresa")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Empresa ABC")));

        verify(customerService).searchCustomers("Empresa", Customer.CustomerStatus.ACTIVE, 0, 10);
    }

    @Test
    @WithMockUser
    void shouldUpdateCustomerSuccessfully() throws Exception {
        when(customerService.updateCustomer(anyLong(), any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Empresa ABC")));

        verify(customerService).updateCustomer(eq(1L), any(CustomerDTO.class));
    }

    @Test
    @WithMockUser
    void shouldDeleteCustomerSuccessfully() throws Exception {
        doNothing().when(customerService).deleteCustomer(anyLong());

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    @WithMockUser
    void shouldChangeCustomerStatusSuccessfully() throws Exception {
        when(customerService.changeCustomerStatus(anyLong(), any(Customer.CustomerStatus.class))).thenReturn(customerDTO);

        mockMvc.perform(patch("/api/v1/customers/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(customerService).changeCustomerStatus(eq(1L), eq(Customer.CustomerStatus.INACTIVE));
    }

    @Test
    @WithMockUser
    void shouldHandleValidationExceptionWhenCreatingCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenThrow(new ValidationException("Já existe um cliente cadastrado com este CNPJ"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Já existe um cliente cadastrado com este CNPJ")));

        verify(customerService).createCustomer(any(CustomerDTO.class));
    }
}