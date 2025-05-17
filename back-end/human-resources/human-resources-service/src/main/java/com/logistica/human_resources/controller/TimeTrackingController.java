// TimeTrackingController.java
package com.logistics.HumanResources.controller;

import com.logistics.HumanResources.dto.TimeEntryDTO;
import com.logistics.HumanResources.service.TimeTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/time-tracking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Time Tracking", description = "APIs for managing employee time entries")
@Validated
public class TimeTrackingController {

    private final TimeTrackingService timeTrackingService;

    @GetMapping
    @Operation(summary = "Get all time entries")
    public ResponseEntity<List<TimeEntryDTO>> getAllTimeEntries() {
        log.info("REST request to get all time entries");
        return ResponseEntity.ok(timeTrackingService.getAllTimeEntries());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get time entry by ID")
    public ResponseEntity<TimeEntryDTO> getTimeEntry(@PathVariable Long id) {
        log.info("REST request to get time entry with id: {}", id);
        return ResponseEntity.ok(timeTrackingService.getTimeEntryById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get time entries by employee ID")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByEmployeeId(@PathVariable Long employeeId) {
        log.info("REST request to get time entries for employee id: {}", employeeId);
        return ResponseEntity.ok(timeTrackingService.getTimeEntriesByEmployeeId(employeeId));
    }

    @GetMapping("/range")
    @Operation(summary = "Get time entries by date range")
    public ResponseEntity<List<TimeEntryDTO>> getTimeEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get time entries between {} and {}", start, end);
        return ResponseEntity.ok(timeTrackingService.getTimeEntriesByDateRange(start, end));
    }

    @PostMapping("/entry")
    @Operation(summary = "Record a new time entry")
    public ResponseEntity<TimeEntryDTO> recordEntry(
            @Valid @RequestBody TimeEntryDTO timeEntryDTO,
            HttpServletRequest request) {

        log.info("REST request to record time entry for employee id: {}", timeEntryDTO.getEmployeeId());

        String ipAddress = request.getRemoteAddr();
        String location = request.getHeader("X-Location"); // Assuming location is sent in a custom header

        TimeEntryDTO result = timeTrackingService.recordEntry(timeEntryDTO, ipAddress, location);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/exit")
    @Operation(summary = "Record exit time for a time entry")
    public ResponseEntity<TimeEntryDTO> recordExit(
            @PathVariable Long id,
            @RequestParam(required = false) String notes,
            HttpServletRequest request) {

        log.info("REST request to record exit for time entry id: {}", id);

        String ipAddress = request.getRemoteAddr();
        String location = request.getHeader("X-Location");

        TimeEntryDTO result = timeTrackingService.recordExit(id, notes, ipAddress, location);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a time entry")
    public ResponseEntity<TimeEntryDTO> updateTimeEntry(
            @PathVariable Long id,
            @Valid @RequestBody TimeEntryDTO timeEntryDTO) {

        log.info("REST request to update time entry with id: {}", id);
        TimeEntryDTO result = timeTrackingService.updateTimeEntry(id, timeEntryDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a time entry")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        log.info("REST request to delete time entry with id: {}", id);
        timeTrackingService.deleteTimeEntry(id);
        return ResponseEntity.noContent().build();
    }
}