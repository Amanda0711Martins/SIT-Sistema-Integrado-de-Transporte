package com.logistica.human_resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.human_resources.dto.PayrollDTO;
import com.logistica.human_resources.model.Payroll;
import com.logistica.human_resources.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayrollController.class)
public class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCalculatePayroll() throws Exception {
        PayrollDTO dto = PayrollDTO.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("João Silva")
                .yearMonth(YearMonth.of(2025, 8))
                .grossSalary(new BigDecimal("7000"))
                .inssDeduction(new BigDecimal("500"))
                .incomeTaxDeduction(new BigDecimal("100"))
                .netSalary(new BigDecimal("6400"))
                .processedAt(LocalDate.now())
                .paymentDate(LocalDate.now())
                .status(Payroll.PayrollStatus.PROCESSED)
                .build();

        Mockito.when(payrollService.calculatePayroll(anyLong(), any(YearMonth.class), any(), any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/payrolls/calculate")
                        .param("employeeId", "1")
                        .param("year", "2025")
                        .param("month", "8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeName").value("João Silva"));
    }
}
