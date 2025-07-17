package com.logistics.financial.controller;

import com.logistics.financial.dto.InvoiceDTO;
import com.logistics.financial.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;
    

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(Pageable pageable) {
        Page<InvoiceDTO> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @Valid @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoice(id, invoiceDTO);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<InvoiceDTO>> getInvoicesByClientId(@PathVariable Long clientId, Pageable pageable) {
        Page<InvoiceDTO> invoices = invoiceService.getInvoicesByClientId(clientId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<InvoiceDTO>> getInvoicesByStatus(@PathVariable String status, Pageable pageable) {
        Page<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<InvoiceDTO> issueInvoice(@PathVariable Long id) {
        InvoiceDTO issuedInvoice = invoiceService.issueInvoice(id);
        return ResponseEntity.ok(issuedInvoice);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<InvoiceDTO> cancelInvoice(@PathVariable Long id) {
        InvoiceDTO cancelledInvoice = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(cancelledInvoice);
    }
}