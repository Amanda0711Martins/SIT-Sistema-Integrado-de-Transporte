// PayrollServiceTest.java
package com.logistics.HumanResources.service;

import com.logistics.HumanResources.dto.PayrollDTO;
import com.logistics.HumanResources.exception.BusinessException;
import com.logistics.HumanResources.exception.ResourceNotFoundException;
import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.model.Payroll;
import com.logistics.HumanResources.model.Payroll.PayrollStatus;
import com.logistics.HumanResources.repository.EmployeeRepository;
import com.logistics.HumanResources.repository.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PayrollService payrollService;

    private Employee employee;
    private Payroll payroll;
    private YearMonth currentMonth;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .encryptedCpf("encrypted123")
                .position("Developer")
                .department("IT")
                .baseSalary(new BigDecimal("5000.00"))
                .hireDate(LocalDate.of(2020, 1, 15))
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .build();

        currentMonth = YearMonth.now();

        payroll = Payroll.builder()
                .id(1L)
                .employee(employee)
                .yearMonth(currentMonth)
                .grossSalary(new BigDecimal("5000.00"))
                .inssDeduction(new BigDecimal("550.00"))
                .incomeTaxDeduction(new BigDecimal("500.00"))
                .netSalary(new BigDecimal("3950.00"))
                .processedAt(LocalDate.now())
                .status(PayrollStatus.PROCESSED)
                .build();
    }

    @Test
    void getAllPayrolls_ShouldReturnListOfPayrolls() {
        // Arrange
        when(payrollRepository.findAll()).thenReturn(Arrays.asList(payroll));

        // Act
        List<PayrollDTO> result = payrollService.getAllPayrolls();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("John Doe");
        assertThat(result.get(0).getGrossSalary()).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    void getPayrollById_WhenPayrollExists_ShouldReturnPayroll() {
        // Arrange
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        // Act
        PayrollDTO result = payrollService.getPayrollById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmployeeName()).isEqualTo("John Doe");
    }

    @Test
    void getPayrollById_WhenPayrollNotExists_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(payrollRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> payrollService.getPayrollById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payroll not found with id: 99");
    }

    @Test
    void calculatePayroll_WhenValidData_ShouldCalculatePayroll() {
        // Arrange
        YearMonth nextMonth = currentMonth.plusMonths(1);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeAndYearMonth(any(), any())).thenReturn(Optional.empty());
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> {
            Payroll savedPayroll = invocation.getArgument(0);
            savedPayroll.setId(1L);
            return savedPayroll;
        });

        BigDecimal bonuses = new BigDecimal("500.00");
        BigDecimal otherDeductions = new BigDecimal("200.00");

        // Act
        PayrollDTO result = payrollService.calculatePayroll(1L, nextMonth, bonuses, otherDeductions);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getYearMonth()).isEqualTo(nextMonth);
        assertThat(result.getBonuses()).isEqualByComparingTo(bonuses);
        assertThat(result.getOtherDeductions()).isEqualByComparingTo(otherDeductions);
        assertThat(result.getStatus()).isEqualTo(PayrollStatus.PROCESSED);

        verify(payrollRepository).save(any(Payroll.class));
    }

    @Test
    void calculatePayroll_WhenPayrollAlreadyExists_ShouldThrowBusinessException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeAndYearMonth(any(), any())).thenReturn(Optional.of(payroll));

        // Act & Assert
        assertThatThrownBy(() -> payrollService.calculatePayroll(1L, currentMonth, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Payroll already exists for employee id: 1 and month: " + currentMonth);

        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    void updatePayrollStatus_WhenPayrollExistsAndStatusValid_ShouldUpdateStatus() {
        // Arrange
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll);

        // Act
        PayrollDTO result = payrollService.updatePayrollStatus(1L, PayrollStatus.PAID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PayrollStatus.PAID);
        assertThat(result.getPaymentDate()).isNotNull();

        verify(payrollRepository).save(any(Payroll.class));
    }

    @Test
    void updatePayrollStatus_WhenInvalidStatusTransition_ShouldThrowBusinessException() {
        // Arrange
        payroll.setStatus(PayrollStatus.DRAFT);
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        // Act & Assert
        assertThatThrownBy(() -> payrollService.updatePayrollStatus(1L, PayrollStatus.PAID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only processed payrolls can be marked as paid");

        verify(payrollRepository, never()).save(any(Payroll.class));
    }
}