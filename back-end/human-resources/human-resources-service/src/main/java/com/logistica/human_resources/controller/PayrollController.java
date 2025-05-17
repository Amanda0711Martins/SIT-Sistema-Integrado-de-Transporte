// PayrollController.java
package com.logistics.HumanResources.controller;

import com.logistics.HumanResources.dto.PayrollDTO;
import com.logistics.HumanResources.model.Payroll;
import com.logistics.HumanResources.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payroll Management", description = "APIs for managing payroll information")
@Validated
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    @Operation(summary = "Get all payrolls")
    public ResponseEntity<List<PayrollDTO>> getAllPayrolls() {
        log.info("REST request to get all payrolls");
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payroll by ID")
    public ResponseEntity<PayrollDTO> getPayroll(@PathVariable Long id) {
        log.info("REST request to get payroll with id: {}", id);
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get payrolls by employee ID")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByEmployee(@PathVariable Long employeeId) {
        log.info("REST request to get payrolls for employee id: {}", employeeId);
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
    }

    @GetMapping("/month/{year}/{month}")
    @Operation(summary = "Get payrolls by year and month")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByYearMonth(
            @PathVariable int year,
            @PathVariable int month) {
        log.info("REST request to get payrolls for year: {} and month: {}", year, month);
        return ResponseEntity.ok(payrollService.getPayrollsByYearMonth(YearMonth.of(year, month)));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate payroll for an employee and month")
    public ResponseEntity<PayrollDTO> calculatePayroll(
            @RequestParam @NotNull Long employeeId,
            @RequestParam @NotNull int year,
            @RequestParam @NotNull int month,
            @RequestParam(required = false) BigDecimal bonuses,
            @RequestParam(required = false) BigDecimal otherDeductions) {

        log.info("REST request to calculate payroll for employee id: {}, year: {}, month: {}",
                employeeId, year, month);

        PayrollDTO result = payrollService.calculatePayroll(
                employeeId,
                YearMonth.of(year, month),
                bonuses,
                otherDeductions);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payroll status")
    public ResponseEntity<PayrollDTO> updatePayrollStatus(
            @PathVariable Long id,
            @RequestParam @NotNull Payroll.PayrollStatus status) {

        log.info("REST request to update status of payroll id: {} to {}", id, status);
        PayrollDTO result = payrollService.updatePayrollStatus(id, status);
        return ResponseEntity.ok(result);
    }
}