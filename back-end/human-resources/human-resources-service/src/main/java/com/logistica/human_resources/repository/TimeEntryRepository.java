// TimeEntryRepository.java
package com.logistics.HumanResources.repository;

import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByEmployeeId(Long employeeId);

    List<TimeEntry> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    List<TimeEntry> findByEmployeeAndEntryTimeBetween(Employee employee, LocalDateTime start, LocalDateTime end);

    Optional<TimeEntry> findTopByEmployeeOrderByEntryTimeDesc(Employee employee);
}