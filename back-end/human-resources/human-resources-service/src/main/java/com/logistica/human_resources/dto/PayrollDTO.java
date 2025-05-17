// PayrollDTO.java
package com.logistics.HumanResources.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.logistics.HumanResources.model.Payroll.PayrollStatus;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

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