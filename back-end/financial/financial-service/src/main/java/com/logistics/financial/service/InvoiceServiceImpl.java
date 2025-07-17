package com.logistics.financial.service;

import com.logistics.financial.client.CustomerServiceClient;
import com.logistics.financial.dto.ClientDTO;
import com.logistics.financial.dto.InvoiceDTO;
import com.logistics.financial.exception.BusinessException;
import com.logistics.financial.exception.ResourceNotFoundException;
import com.logistics.financial.mapper.InvoiceMapper;
import com.logistics.financial.model.Invoice;
import com.logistics.financial.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final CustomerServiceClient clientServiceClient;

    @Override
    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        log.info("Creating new invoice for client: {}", invoiceDTO.getClientId());

        // Validate if client exists
        ClientDTO client = clientServiceClient.getClientById(invoiceDTO.getClientId());

        Invoice invoice = invoiceMapper.toEntity(invoiceDTO);

        // Generate invoice number if not provided
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }

        // Default status to DRAFT if not specified
        if (invoice.getStatus() == null) {
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        InvoiceDTO result = invoiceMapper.toDto(savedInvoice);
        result.setClientName(client.getName());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        log.info("Getting invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        try {
            ClientDTO client = clientServiceClient.getClientById(invoice.getClientId());
            invoiceDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for invoice: {}", id, e);
            invoiceDTO.setClientName("Unknown Client");
        }

        return invoiceDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        log.info("Getting invoice with number: {}", invoiceNumber);
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + invoiceNumber));

        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        try {
            ClientDTO client = clientServiceClient.getClientById(invoice.getClientId());
            invoiceDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for invoice: {}", invoiceNumber, e);
            invoiceDTO.setClientName("Unknown Client");
        }

        return invoiceDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        log.info("Getting all invoices");
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        Page<InvoiceDTO> invoiceDTOs = invoices.map(invoiceMapper::toDto);

        // Enrich with client names
        invoiceDTOs.forEach(this::enrichWithClientName);

        return invoiceDTOs;
    }

    @Override
    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        log.info("Updating invoice with id: {}", id);
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        // Prevent updating ISSUED or PAID invoices
        if (existingInvoice.getStatus() == Invoice.InvoiceStatus.ISSUED ||
                existingInvoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new BusinessException("Cannot update an invoice with status: " + existingInvoice.getStatus());
        }

        Invoice updatedInvoice = invoiceMapper.toEntity(invoiceDTO);
        updatedInvoice.setId(id);
        updatedInvoice.setInvoiceNumber(existingInvoice.getInvoiceNumber());
        updatedInvoice.setCreatedAt(existingInvoice.getCreatedAt());

        Invoice savedInvoice = invoiceRepository.save(updatedInvoice);
        InvoiceDTO result = invoiceMapper.toDto(savedInvoice);

        // Fetch client name
        try {
            ClientDTO client = clientServiceClient.getClientById(savedInvoice.getClientId());
            result.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for invoice: {}", id, e);
            result.setClientName("Unknown Client");
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        log.info("Deleting invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        // Only allow deleting DRAFT invoices
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new BusinessException("Can only delete invoices with DRAFT status. Current status: " + invoice.getStatus());
        }

        invoiceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getInvoicesByClientId(Long clientId, Pageable pageable) {
        log.info("Getting invoices for client: {}", clientId);

        // Verify client exists
        ClientDTO client = clientServiceClient.getClientById(clientId);

        Page<Invoice> invoices = invoiceRepository.findByClientId(clientId, pageable);
        Page<InvoiceDTO> invoiceDTOs = invoices.map(invoiceMapper::toDto);

        // Set client name for all DTOs
        invoiceDTOs.forEach(dto -> dto.setClientName(client.getName()));

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getInvoicesByStatus(String status, Pageable pageable) {
        log.info("Getting invoices with status: {}", status);
        Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status);
        Page<Invoice> invoices = invoiceRepository.findByStatus(invoiceStatus, pageable);
        Page<InvoiceDTO> invoiceDTOs = invoices.map(invoiceMapper::toDto);

        // Enrich with client names
        invoiceDTOs.forEach(this::enrichWithClientName);

        return invoiceDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting invoices between {} and {}", startDate, endDate);
        List<Invoice> invoices = invoiceRepository.findByIssueDateBetween(startDate, endDate);
        List<InvoiceDTO> invoiceDTOs = invoiceMapper.toDtoList(invoices);

        // Enrich with client names
        invoiceDTOs.forEach(this::enrichWithClientName);

        return invoiceDTOs;
    }

    @Override
    @Transactional
    public InvoiceDTO issueInvoice(Long id) {
        log.info("Issuing invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new BusinessException("Only DRAFT invoices can be issued. Current status: " + invoice.getStatus());
        }

        // Here we would integrate with SEFAZ mock API to issue the invoice
        // For demonstration, we'll just generate a random fiscal key
        String fiscalKey = generateFiscalKey();
        invoice.setFiscalKey(fiscalKey);
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        invoice.setIssueDate(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        InvoiceDTO result = invoiceMapper.toDto(savedInvoice);

        // Fetch client name
        enrichWithClientName(result);

        return result;
    }

    @Override
    @Transactional
    public InvoiceDTO cancelInvoice(Long id) {
        log.info("Cancelling invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new BusinessException("Cannot cancel a PAID invoice");
        }

        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        InvoiceDTO result = invoiceMapper.toDto(savedInvoice);

        // Fetch client name
        enrichWithClientName(result);

        return result;
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateFiscalKey() {
        return UUID.randomUUID().toString();
    }

    private void enrichWithClientName(InvoiceDTO invoiceDTO) {
        try {
            ClientDTO client = clientServiceClient.getClientById(invoiceDTO.getClientId());
            invoiceDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for invoice: {}", invoiceDTO.getId(), e);
            invoiceDTO.setClientName("Unknown Client");
        }
    }
}