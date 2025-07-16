// PayrollController.java
package com.logistica.human_resources.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.logistica.human_resources.dto.PayrollDTO;
import com.logistica.human_resources.model.Payroll;
import com.logistica.human_resources.service.PayrollService;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    public ResponseEntity<List<PayrollDTO>> getAllPayrolls() {
        log.info("REST request to get all payrolls");
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollDTO> getPayroll(@PathVariable Long id) {
        log.info("REST request to get payroll with id: {}", id);
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByEmployee(@PathVariable Long employeeId) {
        log.info("REST request to get payrolls for employee id: {}", employeeId);
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
    }

    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByYearMonth(
            @PathVariable int year,
            @PathVariable int month) {
        log.info("REST request to get payrolls for year: {} and month: {}", year, month);
        return ResponseEntity.ok(payrollService.getPayrollsByYearMonth(YearMonth.of(year, month)));
    }

    @PostMapping("/calculate")
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
    public ResponseEntity<PayrollDTO> updatePayrollStatus(
            @PathVariable Long id,
            @RequestParam @NotNull Payroll.PayrollStatus status) {

        log.info("REST request to update status of payroll id: {} to {}", id, status);
        PayrollDTO result = payrollService.updatePayrollStatus(id, status);
        return ResponseEntity.ok(result);
    }
}