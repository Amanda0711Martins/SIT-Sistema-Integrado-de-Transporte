package com.logistica.human_resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.human_resources.dto.TimeEntryDTO;
import com.logistica.human_resources.enums.EntryType;
import com.logistica.human_resources.service.TimeTrackingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeTrackingController.class)
public class TimeTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeTrackingService timeTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRecordEntry() throws Exception {
        TimeEntryDTO dto = TimeEntryDTO.builder()
            .id(1L)
            .employeeId(1L)
            .employeeName("Carlos Lima")
            .entryTime(LocalDateTime.of(2025, 8, 1, 8, 0))
            .entryType(EntryType.ENTRADA)
            .ipAddress("192.168.0.1")
            .location("Escritório")
            .notes("Ponto normal")
            .build();

        Mockito.when(timeTrackingService.recordEntry(any(), anyString(), anyString()))
            .thenReturn(dto);

        mockMvc.perform(post("/api/v1/time-tracking/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.employeeName").value("Carlos Lima"));
    }

    @Test
    void testGetAllTimeEntries() throws Exception {
        TimeEntryDTO entry = TimeEntryDTO.builder()
            .id(1L)
            .employeeId(1L)
            .employeeName("Ana Souza")
            .entryTime(LocalDateTime.now())
            .exitTime(LocalDateTime.now().plusHours(8))
            .entryType(EntryType.WORK)
            .ipAddress("127.0.0.1")
            .location("Escritório")
            .notes("Trabalho normal")
            .build();

        Mockito.when(timeTrackingService.getAllTimeEntries())
            .thenReturn(List.of(entry));

        mockMvc.perform(get("/api/v1/time-tracking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].employeeName").value("Ana Souza"));
    }
}
