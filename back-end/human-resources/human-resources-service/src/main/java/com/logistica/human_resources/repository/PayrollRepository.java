// PayrollRepository.java
package com.logistics.HumanResources.repository;

import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByEmployeeId(Long employeeId);

    List<Payroll> findByYearMonth(YearMonth yearMonth);

    Optional<Payroll> findByEmployeeAndYearMonth(Employee employee, YearMonth yearMonth);

    List<Payroll> findByStatus(Payroll.PayrollStatus status);
}