// TimeEntryDTO.java
package com.logistics.HumanResources.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.logistics.HumanResources.model.TimeEntry.EntryType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDTO {
    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "Entry time is required")
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @NotNull(message = "Entry type is required")
    private EntryType entryType;

    private String notes;

    private String ipAddress;

    private String location;
}