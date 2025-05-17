// TimeTrackingService.java
package com.logistics.HumanResources.service;

import com.logistics.HumanResources.dto.TimeEntryDTO;
import com.logistics.HumanResources.exception.BusinessException;
import com.logistics.HumanResources.exception.ResourceNotFoundException;
import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.model.TimeEntry;
import com.logistics.HumanResources.repository.EmployeeRepository;
import com.logistics.HumanResources.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeTrackingService {
    private final TimeEntryRepository timeEntryRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getAllTimeEntries() {
        log.info("Retrieving all time entries");
        return timeEntryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TimeEntryDTO getTimeEntryById(Long id) {
        log.info("Retrieving time entry with id: {}", id);
        return timeEntryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getTimeEntriesByEmployeeId(Long employeeId) {
        log.info("Retrieving time entries for employee id: {}", employeeId);
        return timeEntryRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getTimeEntriesByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Retrieving time entries between {} and {}", start, end);
        return timeEntryRepository.findByEntryTimeBetween(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeEntryDTO recordEntry(TimeEntryDTO timeEntryDTO, String ipAddress, String location) {
        log.info("Recording time entry for employee id: {}", timeEntryDTO.getEmployeeId());

        Employee employee = employeeRepository.findById(timeEntryDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + timeEntryDTO.getEmployeeId()));

        if (!employee.isActive()) {
            throw new BusinessException("Cannot record time for inactive employee");
        }

        // Check if the employee already has an open entry (entry without exit)
        Optional<TimeEntry> lastEntryOpt = timeEntryRepository.findTopByEmployeeOrderByEntryTimeDesc(employee);

        if (lastEntryOpt.isPresent() && lastEntryOpt.get().getExitTime() == null) {
            throw new BusinessException("Employee has an open time entry. Please close it before creating a new one.");
        }

        TimeEntry timeEntry = TimeEntry.builder()
                .employee(employee)
                .entryTime(timeEntryDTO.getEntryTime() != null ? timeEntryDTO.getEntryTime() : LocalDateTime.now())
                .entryType(timeEntryDTO.getEntryType())
                .notes(timeEntryDTO.getNotes())
                .ipAddress(ipAddress)
                .location(location)
                .build();

        timeEntry = timeEntryRepository.save(timeEntry);
        return convertToDto(timeEntry);
    }

    @Transactional
    public TimeEntryDTO recordExit(Long timeEntryId, String notes, String ipAddress, String location) {
        log.info("Recording exit for time entry id: {}", timeEntryId);

        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + timeEntryId));

        if (timeEntry.getExitTime() != null) {
            throw new BusinessException("Exit time already recorded for this entry");
        }

        timeEntry.setExitTime(LocalDateTime.now());

        // Update notes if provided
        if (notes != null && !notes.trim().isEmpty()) {
            timeEntry.setNotes(notes);
        }

        // Update location data
        timeEntry.setIpAddress(ipAddress);
        timeEntry.setLocation(location);

        timeEntry = timeEntryRepository.save(timeEntry);
        return convertToDto(timeEntry);
    }

    @Transactional
    public TimeEntryDTO updateTimeEntry(Long id, TimeEntryDTO timeEntryDTO) {
        log.info("Updating time entry with id: {}", id);

        TimeEntry timeEntry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found with id: " + id));

        // Only allow updates to certain fields
        if (timeEntryDTO.getEntryType() != null) {
            timeEntry.setEntryType(timeEntryDTO.getEntryType());
        }

        if (timeEntryDTO.getNotes() != null) {
            timeEntry.setNotes(timeEntryDTO.getNotes());
        }

        // Only administrators should be able to modify timestamps
        // This would typically be controlled by authentication/authorization
        if (timeEntryDTO.getEntryTime() != null) {
            timeEntry.setEntryTime(timeEntryDTO.getEntryTime());
        }

        if (timeEntryDTO.getExitTime() != null) {
            timeEntry.setExitTime(timeEntryDTO.getExitTime());
        }

        timeEntry = timeEntryRepository.save(timeEntry);
        return convertToDto(timeEntry);
    }

    @Transactional
    public void deleteTimeEntry(Long id) {
        log.info("Deleting time entry with id: {}", id);

        if (!timeEntryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Time entry not found with id: " + id);
        }

        timeEntryRepository.deleteById(id);
    }

    // Conversion methods
    private TimeEntryDTO convertToDto(TimeEntry timeEntry) {
        return TimeEntryDTO.builder()
                .id(timeEntry.getId())
                .employeeId(timeEntry.getEmployee().getId())
                .employeeName(timeEntry.getEmployee().getName())
                .entryTime(timeEntry.getEntryTime())
                .exitTime(timeEntry.getExitTime())
                .entryType(timeEntry.getEntryType())
                .notes(timeEntry.getNotes())
                .ipAddress(timeEntry.getIpAddress())
                .location(timeEntry.getLocation())
                .build();
    }
}