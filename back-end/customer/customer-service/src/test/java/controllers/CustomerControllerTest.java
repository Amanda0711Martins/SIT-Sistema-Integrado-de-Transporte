package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.customer.controller.CustomerController;
import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.dto.CustomerPageResponseDTO;
import com.logistica.customer.exception.CustomerNotFoundException;
import com.logistica.customer.model.Customer;
import com.logistica.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Ferramenta para simular requisições HTTP

    @MockBean
    private CustomerService customerService; // Mock da camada de serviço

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos em JSON

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("Cliente Teste");
        customerDTO.setCnpj("12345678000199");
        customerDTO.setEmail("teste@cliente.com");
    }

    @Test
    void testCreateCustomer_shouldReturn201() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cliente Teste"));
    }

    @Test
    void testGetCustomerById_shouldReturn200_whenCustomerExists() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerDTO);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
    
    @Test
    void testGetCustomerById_shouldReturn404_whenCustomerNotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new CustomerNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCustomer_shouldReturn200() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(put("/api/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente Teste"));
    }

    @Test
    void testDeleteCustomer_shouldReturn204() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testChangeCustomerStatus_shouldReturn200() throws Exception {
        when(customerService.changeCustomerStatus(1L, Customer.CustomerStatus.INACTIVE)).thenReturn(customerDTO);

        mockMvc.perform(patch("/api/v1/customers/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente Teste"));
    }

    @Test
    void testGetAllCustomers_shouldReturn200() throws Exception {
        CustomerPageResponseDTO pageResponse = new CustomerPageResponseDTO(Collections.singletonList(customerDTO), 0, 10, 1, 1, true);
        when(customerService.getAllCustomers(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Cliente Teste"));
    }
}
