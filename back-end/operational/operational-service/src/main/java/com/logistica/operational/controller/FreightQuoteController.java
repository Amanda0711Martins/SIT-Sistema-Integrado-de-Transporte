package com.logistica.operational.controller;

import com.logistica.operational.dto.FreightQuoteRequestDTO;
import com.logistica.operational.dto.FreightQuoteResponseDTO;
import com.logistica.operational.service.FreightQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class FreightQuoteController {

    private final FreightQuoteService quoteService;

    @PostMapping("/calculate")
    public ResponseEntity<FreightQuoteResponseDTO> calculateQuote(@RequestBody FreightQuoteRequestDTO request) {
        return ResponseEntity.ok(quoteService.calculateAndSaveQuote(request));
    }
}