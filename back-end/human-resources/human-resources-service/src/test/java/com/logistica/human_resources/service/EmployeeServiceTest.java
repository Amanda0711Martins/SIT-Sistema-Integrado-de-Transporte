// EmployeeServiceTest.java
package com.logistics.HumanResources.service;

import com.logistics.HumanResources.dto.EmployeeDTO;
import com.logistics.HumanResources.exception.BusinessException;
import com.logistics.HumanResources.exception.ResourceNotFoundException;
import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.repository.EmployeeRepository;
import com.logistics.HumanResources.util.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CryptoUtil cryptoUtil;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee1 = Employee.builder()
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

        employee2 = Employee.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .encryptedCpf("encrypted456")
                .position("Manager")
                .department("IT")
                .baseSalary(new BigDecimal("7000.00"))
                .hireDate(LocalDate.of(2018, 3, 10))
                .birthDate(LocalDate.of(1985, 8, 15))
                .active(true)
                .build();

        employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .cpf("123.456.789-00")
                .position("Developer")
                .department("IT")
                .baseSalary(new BigDecimal("5000.00"))
                .hireDate(LocalDate.of(2020, 1, 15))
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .build();
    }

    @Test
    void getAllEmployees_ShouldReturnPageOfEmployees() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee1, employee2);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        // Act
        Page<EmployeeDTO> result = employeeService.getAllEmployees(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John Doe");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void getActiveEmployees_ShouldReturnListOfActiveEmployees() {
        // Arrange
        when(employeeRepository.findByActiveTrue()).thenReturn(Arrays.asList(employee1, employee2));

        // Act
        List<EmployeeDTO> result = employeeService.getActiveEmployees();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void getEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        // Act
        EmployeeDTO result = employeeService.getEmployeeById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void getEmployeeById_WhenEmployeeNotExists_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: 99");
    }

    @Test
    void createEmployee_WhenEmailIsUnique_ShouldCreateEmployee() {
        // Arrange
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(cryptoUtil.encrypt(anyString())).thenReturn("encrypted123");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        // Act
        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WhenEmailExists_ShouldThrowBusinessException() {
        // Arrange
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(employeeDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already in use");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_WhenEmployeeExistsAndEmailIsValid_ShouldUpdateEmployee() {
        // Arrange
        EmployeeDTO updateDTO = EmployeeDTO.builder()
                .id(1L)
                .name("John Doe Updated")
                .email("john.doe@example.com")
                .position("Senior Developer")
                .department("IT")
                .baseSalary(new BigDecimal("6000.00"))
                .hireDate(LocalDate.of(2020, 1, 15))
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmployeeDTO result = employeeService.updateEmployee(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe Updated");
        assertThat(result.getPosition()).isEqualTo("Senior Developer");
        assertThat(result.getBaseSalary()).isEqualByComparingTo(new BigDecimal("6000.00"));

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_WhenEmployeeExists_ShouldSoftDeleteEmployee() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee savedEmployee = invocation.getArgument(0);
            assertThat(savedEmployee.isActive()).isFalse();
            return savedEmployee;
        });

        // Act
        employeeService.deleteEmployee(1L);

        // Assert
        verify(employeeRepository).save(any(Employee.class));
    }
}