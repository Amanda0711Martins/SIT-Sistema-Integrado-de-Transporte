// EmployeeService.java
package com.logistics.HumanResources.service;

import com.logistics.HumanResources.dto.EmployeeDTO;
import com.logistics.HumanResources.exception.BusinessException;
import com.logistics.HumanResources.exception.ResourceNotFoundException;
import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.repository.EmployeeRepository;
import com.logistics.HumanResources.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CryptoUtil cryptoUtil;

    @Transactional(readOnly = true)
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        log.info("Retrieving all employees with pagination");
        return employeeRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getActiveEmployees() {
        log.info("Retrieving all active employees");
        return employeeRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Retrieving employee with id: {}", id);
        return employeeRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee: {}", employeeDTO.getEmail());

        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        Employee employee = convertToEntity(employeeDTO);
        employee.setEncryptedCpf(cryptoUtil.encrypt(employeeDTO.getCpf()));
        employee.setCpf(null); // Clear the plain text CPF
        employee = employeeRepository.save(employee);

        return convertToDto(employee);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmail().equals(employeeDTO.getEmail()) &&
                employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        // Update employee data
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPosition(employeeDTO.getPosition());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setBaseSalary(employeeDTO.getBaseSalary());
        employee.setHireDate(employeeDTO.getHireDate());
        employee.setBirthDate(employeeDTO.getBirthDate());
        employee.setActive(employeeDTO.isActive());

        // Only update CPF if provided
        if (employeeDTO.getCpf() != null && !employeeDTO.getCpf().trim().isEmpty()) {
            employee.setEncryptedCpf(cryptoUtil.encrypt(employeeDTO.getCpf()));
        }

        employee = employeeRepository.save(employee);
        return convertToDto(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Soft deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        log.info("Retrieving employees by department: {}", department);
        return employeeRepository.findByDepartment(department)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper methods to convert between Entity and DTO
    private EmployeeDTO convertToDto(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .baseSalary(employee.getBaseSalary())
                .hireDate(employee.getHireDate())
                .birthDate(employee.getBirthDate())
                .active(employee.isActive())
                // We don't return the CPF in the DTO for security reasons
                .build();
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        return Employee.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .position(dto.getPosition())
                .department(dto.getDepartment())
                .baseSalary(dto.getBaseSalary())
                .hireDate(dto.getHireDate())
                .birthDate(dto.getBirthDate())
                .active(dto.isActive())
                .build();
    }
}