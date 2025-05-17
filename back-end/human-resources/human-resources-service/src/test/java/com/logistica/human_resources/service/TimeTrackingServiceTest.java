// TimeTrackingServiceTest.java
package com.logistics.HumanResources.service;

import com.logistics.HumanResources.dto.TimeEntryDTO;
import com.logistics.HumanResources.exception.BusinessException;
import com.logistics.HumanResources.exception.ResourceNotFoundException;
import com.logistics.HumanResources.model.Employee;
import com.logistics.HumanResources.model.TimeEntry;
import com.logistics.HumanResources.model.TimeEntry.EntryType;
import com.logistics.HumanResources.repository.EmployeeRepository;
import com.logistics.HumanResources.repository.TimeEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeTrackingServiceTest {

    @Mock
    private TimeEntryRepository timeEntryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TimeTrackingService timeTrackingService;

    private Employee employee;
    private TimeEntry timeEntry;
    private TimeEntryDTO timeEntryDTO;

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

        LocalDateTime now = LocalDateTime.now();

        timeEntry = TimeEntry.builder()
                .id(1L)
                .employee(employee)
                .entryTime(now)
                .entryType(EntryType.REGULAR)
                .ipAddress("192.168.1.1")
                .location("Office")
                .build();

        timeEntryDTO = TimeEntryDTO.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("John Doe")
                .entryTime(now)
                .entryType(EntryType.REGULAR)
                .ipAddress("192.168.1.1")
                .location("Office")
                .build();
    }

    @Test
    void getAllTimeEntries_ShouldReturnListOfTimeEntries() {
        // Arrange
        when(timeEntryRepository.findAll()).thenReturn(Arrays.asList(timeEntry));

        // Act
        List<TimeEntryDTO> result = timeTrackingService.getAllTimeEntries();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("John Doe");
        assertThat(result.get(0).getEntryType()).isEqualTo(EntryType.REGULAR);
    }

    @Test
    void getTimeEntryById_WhenTimeEntryExists_ShouldReturnTimeEntry() {
        // Arrange
        when(timeEntryRepository.findById(1L)).thenReturn(Optional.of(timeEntry));

        // Act
        TimeEntryDTO result = timeTrackingService.getTimeEntryById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmployeeName()).isEqualTo("John Doe");
    }

    @Test
    void getTimeEntryById_WhenTimeEntryNotExists_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(timeEntryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> timeTrackingService.getTimeEntryById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Time entry not found with id: 99");
    }

    @Test
    void recordEntry_WhenValidData_ShouldCreateTimeEntry() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(timeEntryRepository.findTopByEmployeeOrderByEntryTimeDesc(any())).thenReturn(Optional.empty());
        when(timeEntryRepository.save(any(TimeEntry.class))).thenAnswer(invocation -> {
            TimeEntry savedEntry = invocation.getArgument(0);
            savedEntry.setId(1L);
            return savedEntry;
        });

        // Act
        TimeEntryDTO result = timeTrackingService.recordEntry(timeEntryDTO, "192.168.1.1", "Office");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getEntryTime()).isNotNull();
        assertThat(result.getIpAddress()).isEqualTo("192.168.1.1");

        verify(timeEntryRepository).save(any(TimeEntry.class));
    }

    @Test
    void recordEntry_WhenEmployeeHasOpenEntry_ShouldThrowBusinessException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(timeEntryRepository.findTopByEmployeeOrderByEntryTimeDesc(any())).thenReturn(Optional.of(timeEntry));

        // Act & Assert
        assertThatThrownBy(() -> timeTrackingService.recordEntry(timeEntryDTO, "192.168.1.1", "Office"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee has an open time entry");

        verify(timeEntryRepository, never()).save(any(TimeEntry.class));
    }

    @Test
    void recordExit_WhenValidData_ShouldUpdateTimeEntry() {
        // Arrange
        when(timeEntryRepository.findById(1L)).thenReturn(Optional.of(timeEntry));
        when(timeEntryRepository.save(any(TimeEntry.class))).thenAnswer(invocation -> {
            TimeEntry savedEntry = invocation.getArgument(0);
            savedEntry.setExitTime(LocalDateTime.now());
            return savedEntry;
        });

        // Act
        TimeEntryDTO result = timeTrackingService.recordExit(1L, "Work completed", "192.168.1.1", "Office");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getExitTime()).isNotNull();
        assertThat(result.getNotes()).isEqualTo("Work completed");

        verify(timeEntryRepository).save(any(TimeEntry.class));
    }

    @Test
    void recordExit_WhenExitTimeAlreadySet_ShouldThrowBusinessException() {
        // Arrange
        timeEntry.setExitTime(LocalDateTime.now());
        when(timeEntryRepository.findById(1L)).thenReturn(Optional.of(timeEntry));

        // Act & Assert
        assertThatThrownBy(() -> timeTrackingService.recordExit(1L, "Work completed", "192.168.1.1", "Office"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Exit time already recorded for this entry");

        verify(timeEntryRepository, never()).save(any(TimeEntry.class));
    }

    @Test
    void updateTimeEntry_WhenValidData_ShouldUpdateTimeEntry() {
        // Arrange
        when(timeEntryRepository.findById(1L)).thenReturn(Optional.of(timeEntry));
        when(timeEntryRepository.save(any(TimeEntry.class))).thenReturn(timeEntry);

        TimeEntryDTO updateDTO = TimeEntryDTO.builder()
                .id(1L)
                .employeeId(1L)
                .entryType(EntryType.OVERTIME)
                .notes("Overtime work")
                .build();

        // Act
        TimeEntryDTO result = timeTrackingService.updateTimeEntry(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEntryType()).isEqualTo(EntryType.OVERTIME);
        assertThat(result.getNotes()).isEqualTo("Overtime work");

        verify(timeEntryRepository).save(any(TimeEntry.class));
    }

    @Test
    void deleteTimeEntry_WhenTimeEntryExists_ShouldDeleteTimeEntry() {
        // Arrange
        when(timeEntryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(timeEntryRepository).deleteById(1L);

        // Act
        timeTrackingService.deleteTimeEntry(1L);

        // Assert
        verify(timeEntryRepository).deleteById(1L);
    }

    @Test
    void deleteTimeEntry_WhenTimeEntryNotExists_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(timeEntryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> timeTrackingService.deleteTimeEntry(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Time entry not found with id: 99");

        verify(timeEntryRepository, never()).deleteById(anyLong());
    }
}