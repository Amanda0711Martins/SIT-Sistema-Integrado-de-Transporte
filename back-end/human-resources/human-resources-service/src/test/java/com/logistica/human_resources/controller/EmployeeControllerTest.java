package com.logistica.human_resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.human_resources.dto.EmployeeDTO;
import com.logistica.human_resources.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .name("João da Silva")
                .cpf("123.456.789-00")
                .email("joao@example.com")
                .position("Analista")
                .department("TI")
                .baseSalary(BigDecimal.valueOf(5000))
                .hireDate(LocalDate.now())
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();
    }

    @Test
    void testGetAllEmployees() throws Exception {
        Mockito.when(employeeService.getAllEmployees(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employeeDTO)));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("João da Silva"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Mockito.when(employeeService.getEmployeeById(1L)).thenReturn(employeeDTO);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        Mockito.when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Mockito.when(employeeService.updateEmployee(eq(1L), any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Analista"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(employeeService).deleteEmployee(1L);
    }
}
