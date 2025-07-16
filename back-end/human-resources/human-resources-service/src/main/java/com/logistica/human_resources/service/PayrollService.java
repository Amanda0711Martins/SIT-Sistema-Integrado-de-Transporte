// PayrollService.java
package com.logistica.human_resources.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.human_resources.dto.PayrollDTO;
import com.logistica.human_resources.exception.BusinessException;
import com.logistica.human_resources.exception.ResourceNotFoundException;
import com.logistica.human_resources.model.Employee;
import com.logistica.human_resources.model.Payroll;
import com.logistica.human_resources.model.Payroll.PayrollStatus;
import com.logistica.human_resources.repository.EmployeeRepository;
import com.logistica.human_resources.repository.PayrollRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<PayrollDTO> getAllPayrolls() {
        log.info("Retrieving all payrolls");
        return payrollRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayrollDTO getPayrollById(Long id) {
        log.info("Retrieving payroll with id: {}", id);
        return payrollRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<PayrollDTO> getPayrollsByEmployee(Long employeeId) {
        log.info("Retrieving payrolls for employee id: {}", employeeId);
        return payrollRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollDTO> getPayrollsByYearMonth(YearMonth yearMonth) {
        log.info("Retrieving payrolls for year-month: {}", yearMonth);
        return payrollRepository.findByYearMonth(yearMonth).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PayrollDTO calculatePayroll(Long employeeId, YearMonth yearMonth, BigDecimal bonuses, BigDecimal otherDeductions) {
        log.info("Calculating payroll for employee id: {}, year-month: {}", employeeId, yearMonth);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if payroll already exists for this employee and month
        if (payrollRepository.findByEmployeeAndYearMonth(employee, yearMonth).isPresent()) {
            throw new BusinessException("Payroll already exists for employee id: " + employeeId + " and month: " + yearMonth);
        }

        // Get base salary
        BigDecimal grossSalary = employee.getBaseSalary();
        if (grossSalary == null || grossSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Employee has no valid base salary defined");
        }

        // Calculate INSS deduction (Social Security)
        BigDecimal inssDeduction = calculateInss(grossSalary);

        // Calculate Income Tax deduction
        BigDecimal baseForIncomeTax = grossSalary.subtract(inssDeduction);
        BigDecimal incomeTaxDeduction = calculateIncomeTax(baseForIncomeTax);

        // Calculate net salary
        BigDecimal netSalary = grossSalary
                .subtract(inssDeduction)
                .subtract(incomeTaxDeduction)
                .subtract(otherDeductions != null ? otherDeductions : BigDecimal.ZERO)
                .add(bonuses != null ? bonuses : BigDecimal.ZERO);

        // Create payroll entity
        Payroll payroll = Payroll.builder()
                .employee(employee)
                .yearMonth(yearMonth)
                .grossSalary(grossSalary)
                .inssDeduction(inssDeduction)
                .incomeTaxDeduction(incomeTaxDeduction)
                .otherDeductions(otherDeductions)
                .bonuses(bonuses)
                .netSalary(netSalary)
                .processedAt(LocalDate.now())
                .status(PayrollStatus.PROCESSED)
                .build();

        payroll = payrollRepository.save(payroll);
        return convertToDto(payroll);
    }

    @Transactional
    public PayrollDTO updatePayrollStatus(Long payrollId, PayrollStatus newStatus) {
        log.info("Updating payroll status for id: {} to status: {}", payrollId, newStatus);

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + payrollId));

        if (PayrollStatus.PAID.equals(newStatus) && !PayrollStatus.PROCESSED.equals(payroll.getStatus())) {
            throw new BusinessException("Only processed payrolls can be marked as paid");
        }

        if (PayrollStatus.PAID.equals(payroll.getStatus())) {
            throw new BusinessException("Paid payrolls cannot be modified");
        }

        payroll.setStatus(newStatus);

        if (PayrollStatus.PAID.equals(newStatus)) {
            payroll.setPaymentDate(LocalDate.now());
        }

        payroll = payrollRepository.save(payroll);
        return convertToDto(payroll);
    }

    // Helper methods for tax calculations
    private BigDecimal calculateInss(BigDecimal salary) {
        // Simplified INSS calculation (2023 rates)
        // In a real application, this would be more complex and configurable
        BigDecimal inss;

        if (salary.compareTo(new BigDecimal("1320.00")) <= 0) {
            inss = salary.multiply(new BigDecimal("0.075")); // 7.5%
        } else if (salary.compareTo(new BigDecimal("2571.29")) <= 0) {
            inss = salary.multiply(new BigDecimal("0.09")); // 9%
        } else if (salary.compareTo(new BigDecimal("3856.94")) <= 0) {
            inss = salary.multiply(new BigDecimal("0.12")); // 12%
        } else if (salary.compareTo(new BigDecimal("7507.49")) <= 0) {
            inss = salary.multiply(new BigDecimal("0.14")); // 14%
        } else {
            inss = new BigDecimal("1051.05"); // Maximum INSS contribution
        }

        return inss.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateIncomeTax(BigDecimal baseValue) {
        // Simplified Income Tax calculation (2023 rates)
        // In a real application, this would be more complex and configurable
        BigDecimal incomeTax;

        if (baseValue.compareTo(new BigDecimal("2112.00")) <= 0) {
            incomeTax = BigDecimal.ZERO; // Exempt
        } else if (baseValue.compareTo(new BigDecimal("2826.65")) <= 0) {
            incomeTax = baseValue.multiply(new BigDecimal("0.075")).subtract(new BigDecimal("158.40")); // 7.5%
        } else if (baseValue.compareTo(new BigDecimal("3751.05")) <= 0) {
            incomeTax = baseValue.multiply(new BigDecimal("0.15")).subtract(new BigDecimal("370.40")); // 15%
        } else if (baseValue.compareTo(new BigDecimal("4664.68")) <= 0) {
            incomeTax = baseValue.multiply(new BigDecimal("0.225")).subtract(new BigDecimal("651.73")); // 22.5%
        } else {
            incomeTax = baseValue.multiply(new BigDecimal("0.275")).subtract(new BigDecimal("884.96")); // 27.5%
        }

        return incomeTax.setScale(2, RoundingMode.HALF_UP);
    }

    // Conversion methods
    private PayrollDTO convertToDto(Payroll payroll) {
        return PayrollDTO.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployee().getId())
                .employeeName(payroll.getEmployee().getName())
                .yearMonth(payroll.getYearMonth())
                .grossSalary(payroll.getGrossSalary())
                .inssDeduction(payroll.getInssDeduction())
                .incomeTaxDeduction(payroll.getIncomeTaxDeduction())
                .otherDeductions(payroll.getOtherDeductions())
                .bonuses(payroll.getBonuses())
                .netSalary(payroll.getNetSalary())
                .paymentDate(payroll.getPaymentDate())
                .processedAt(payroll.getProcessedAt())
                .status(payroll.getStatus())
                .build();
    }
}