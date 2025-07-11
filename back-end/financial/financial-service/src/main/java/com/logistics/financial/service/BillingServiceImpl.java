package com.logistics.financial.service;

import com.logistics.financial.client.ClientServiceClient;
import com.logistics.financial.dto.BillingDTO;
import com.logistics.financial.dto.ClientDTO;
import com.logistics.financial.exception.BusinessException;
import com.logistics.financial.exception.ResourceNotFoundException;
import com.logistics.financial.mapper.BillingMapper;
import com.logistics.financial.model.Billing;
import com.logistics.financial.repository.BillingRepository;
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
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;
    private final ClientServiceClient clientServiceClient;

    @Override
    @Transactional
    public BillingDTO createBilling(BillingDTO billingDTO) {
        log.info("Creating new billing for client: {}", billingDTO.getClientId());

        // Validate if client exists
        ClientDTO client = clientServiceClient.getClientById(billingDTO.getClientId());

        Billing billing = billingMapper.toEntity(billingDTO);

        // Generate billing number if not provided
        if (billing.getBillingNumber() == null || billing.getBillingNumber().isEmpty()) {
            billing.setBillingNumber(generateBillingNumber());
        }

        // Default status to PENDING if not specified
        if (billing.getStatus() == null) {
            billing.setStatus(Billing.BillingStatus.PENDING);
        }

        // Generate barcode
        billing.setBarCode(generateBarCode());

        Billing savedBilling = billingRepository.save(billing);
        BillingDTO result = billingMapper.toDto(savedBilling);
        result.setClientName(client.getName());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BillingDTO getBillingById(Long id) {
        log.info("Getting billing with id: {}", id);
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        BillingDTO billingDTO = billingMapper.toDto(billing);

        try {
            ClientDTO client = clientServiceClient.getClientById(billing.getClientId());
            billingDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for billing: {}", id, e);
            billingDTO.setClientName("Unknown Client");
        }

        return billingDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public BillingDTO getBillingByNumber(String billingNumber) {
        log.info("Getting billing with number: {}", billingNumber);
        Billing billing = billingRepository.findByBillingNumber(billingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with number: " + billingNumber));

        BillingDTO billingDTO = billingMapper.toDto(billing);

        try {
            ClientDTO client = clientServiceClient.getClientById(billing.getClientId());
            billingDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for billing: {}", billingNumber, e);
            billingDTO.setClientName("Unknown Client");
        }

        return billingDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingDTO> getAllBillings(Pageable pageable) {
        log.info("Getting all billings");
        Page<Billing> billings = billingRepository.findAll(pageable);
        Page<BillingDTO> billingDTOs = billings.map(billingMapper::toDto);

        // Enrich with client names
        billingDTOs.forEach(this::enrichWithClientName);

        return billingDTOs;
    }

    @Override
    @Transactional
    public BillingDTO updateBilling(Long id, BillingDTO billingDTO) {
        log.info("Updating billing with id: {}", id);
        Billing existingBilling = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        // Prevent updating PAID or CANCELLED billings
        if (existingBilling.getStatus() == Billing.BillingStatus.PAID ||
                existingBilling.getStatus() == Billing.BillingStatus.CANCELLED) {
            throw new BusinessException("Cannot update a billing with status: " + existingBilling.getStatus());
        }

        Billing updatedBilling = billingMapper.toEntity(billingDTO);
        updatedBilling.setId(id);
        updatedBilling.setBillingNumber(existingBilling.getBillingNumber());
        updatedBilling.setBarCode(existingBilling.getBarCode());
        updatedBilling.setCreatedAt(existingBilling.getCreatedAt());

        Billing savedBilling = billingRepository.save(updatedBilling);
        BillingDTO result = billingMapper.toDto(savedBilling);

        // Fetch client name
        enrichWithClientName(result);

        return result;
    }

    @Override
    @Transactional
    public void deleteBilling(Long id) {
        log.info("Deleting billing with id: {}", id);
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        // Only allow deleting PENDING billings
        if (billing.getStatus() != Billing.BillingStatus.PENDING) {
            throw new BusinessException("Can only delete billings with PENDING status. Current status: " + billing.getStatus());
        }

        billingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingDTO> getBillingsByClientId(Long clientId, Pageable pageable) {
        log.info("Getting billings for client: {}", clientId);

        // Verify client exists
        ClientDTO client = clientServiceClient.getClientById(clientId);

        Page<Billing> billings = billingRepository.findByClientId(clientId, pageable);
        Page<BillingDTO> billingDTOs = billings.map(billingMapper::toDto);

        // Set client name for all DTOs
        billingDTOs.forEach(dto -> dto.setClientName(client.getName()));

        return billingDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingDTO> getBillingsByStatus(String status, Pageable pageable) {
        log.info("Getting billings with status: {}", status);
        Billing.BillingStatus billingStatus = Billing.BillingStatus.valueOf(status);
        Page<Billing> billings = billingRepository.findByStatus(billingStatus, pageable);
        Page<BillingDTO> billingDTOs = billings.map(billingMapper::toDto);

        // Enrich with client names
        billingDTOs.forEach(this::enrichWithClientName);

        return billingDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingDTO> getOverdueBillings() {
        log.info("Getting overdue billings");
        LocalDateTime now = LocalDateTime.now();
        List<Billing> overdueBillings = billingRepository.findByDueDateBefore(now);

        // Update status to OVERDUE if still PENDING
        overdueBillings.stream()
                .filter(billing -> billing.getStatus() == Billing.BillingStatus.PENDING)
                .forEach(billing -> {
                    billing.setStatus(Billing.BillingStatus.OVERDUE);
                    billingRepository.save(billing);
                });

        List<BillingDTO> billingDTOs = billingMapper.toDtoList(overdueBillings);

        // Enrich with client names
        billingDTOs.forEach(this::enrichWithClientName);

        return billingDTOs;
    }

    @Override
    @Transactional
    public BillingDTO markAsPaid(Long id, String paymentMethod) {
        log.info("Marking billing with id {} as paid with payment method: {}", id, paymentMethod);
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        if (billing.getStatus() == Billing.BillingStatus.CANCELLED) {
            throw new BusinessException("Cannot mark a cancelled billing as paid");
        }

        if (billing.getStatus() == Billing.BillingStatus.PAID) {
            throw new BusinessException("Billing is already marked as paid");
        }

        billing.setStatus(Billing.BillingStatus.PAID);
        billing.setPaymentMethod(paymentMethod);

        Billing savedBilling = billingRepository.save(billing);
        BillingDTO result = billingMapper.toDto(savedBilling);

        // Fetch client name
        enrichWithClientName(result);

        return result;
    }

    @Override
    @Transactional
    public BillingDTO cancelBilling(Long id) {
        log.info("Cancelling billing with id: {}", id);
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        if (billing.getStatus() == Billing.BillingStatus.PAID) {
            throw new BusinessException("Cannot cancel a paid billing");
        }

        billing.setStatus(Billing.BillingStatus.CANCELLED);

        Billing savedBilling = billingRepository.save(billing);
        BillingDTO result = billingMapper.toDto(savedBilling);

        // Fetch client name
        enrichWithClientName(result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingDTO> getBillingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting billings between {} and {}", startDate, endDate);
        List<Billing> billings = billingRepository.findByIssueDateBetween(startDate, endDate);
        List<BillingDTO> billingDTOs = billingMapper.toDtoList(billings);

        // Enrich with client names
        billingDTOs.forEach(this::enrichWithClientName);

        return billingDTOs;
    }

    private String generateBillingNumber() {
        return "BIL-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateBarCode() {
        // This is a simplified barcode generation - in a real system,
        // you would follow specific barcode standards for financial documents
        return UUID.randomUUID().toString();
    }

    private void enrichWithClientName(BillingDTO billingDTO) {
        try {
            ClientDTO client = clientServiceClient.getClientById(billingDTO.getClientId());
            billingDTO.setClientName(client.getName());
        } catch (Exception e) {
            log.warn("Could not fetch client details for billing: {}", billingDTO.getId(), e);
            billingDTO.setClientName("Unknown Client");
        }
    }
}