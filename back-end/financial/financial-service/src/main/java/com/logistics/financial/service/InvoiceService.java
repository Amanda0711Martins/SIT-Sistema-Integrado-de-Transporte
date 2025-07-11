package com.logistics.financial.service;

import com.logistics.financial.dto.InvoiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);
    InvoiceDTO getInvoiceById(Long id);
    InvoiceDTO getInvoiceByNumber(String invoiceNumber);
    Page<InvoiceDTO> getAllInvoices(Pageable pageable);
    InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO);
    void deleteInvoice(Long id);
    Page<InvoiceDTO> getInvoicesByClientId(Long clientId, Pageable pageable);
    Page<InvoiceDTO> getInvoicesByStatus(String status, Pageable pageable);
    List<InvoiceDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    InvoiceDTO issueInvoice(Long id);
    InvoiceDTO cancelInvoice(Long id);
}