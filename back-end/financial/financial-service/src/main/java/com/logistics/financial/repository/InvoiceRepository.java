package com.logistics.financial.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistics.financial.model.Invoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByClientId(Long clientId, Pageable pageable);
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);
    List<Invoice> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> findByClientIdAndStatus(Long clientId, Invoice.InvoiceStatus status);
}