// PayrollDTO.java
package com.logistica.human_resources.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import com.logistica.human_resources.model.Payroll.PayrollStatus;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDTO {
    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "Year month is required")
    private YearMonth yearMonth;

    private BigDecimal grossSalary;

    private BigDecimal inssDeduction;

    private BigDecimal incomeTaxDeduction;

    private BigDecimal otherDeductions;

    private BigDecimal bonuses;

    private BigDecimal netSalary;

    private LocalDate paymentDate;

    private LocalDate processedAt;

    private PayrollStatus status;
}