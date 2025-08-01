// TimeEntryDTO.java
package com.logistica.human_resources.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import com.logistica.human_resources.enums.EntryType; // <- Enum do pacote correto

import jakarta.validation.constraints.NotNull;

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
    private EntryType entryType; // <- Enum corrigido

    private String notes;

    private String ipAddress;

    private String location;
}
