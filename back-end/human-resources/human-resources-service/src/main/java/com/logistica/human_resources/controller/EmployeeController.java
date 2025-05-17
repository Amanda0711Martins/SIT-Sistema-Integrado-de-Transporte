// EmployeeController.java
package com.logistics.HumanResources.controller;

import com.logistics.HumanResources.dto.EmployeeDTO;
import com.logistics.HumanResources.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Management", description = "APIs for managing employee information")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees with pagination")
    public ResponseEntity<Page<EmployeeDTO>> getAllEmployees(Pageable pageable) {
        log.info("REST request to get all employees with pagination");
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active employees")
    public ResponseEntity<List<EmployeeDTO>> getActiveEmployees() {
        log.info("REST request to get all active employees");
        return ResponseEntity.ok(employeeService.getActiveEmployees());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        log.info("REST request to get employee with id: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable String department) {
        log.info("REST request to get employees by department: {}", department);
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("REST request to create employee: {}", employeeDTO.getEmail());
        EmployeeDTO result = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing employee")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("REST request to update employee with id: {}", id);
        EmployeeDTO result = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee (soft delete)")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("REST request to delete employee with id: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}