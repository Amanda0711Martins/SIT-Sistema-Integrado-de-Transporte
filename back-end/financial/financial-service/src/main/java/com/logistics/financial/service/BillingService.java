package com.logistics.financial.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.logistics.financial.dto.BillingDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BillingService {
    BillingDTO createBilling(BillingDTO billingDTO);
    BillingDTO getBillingById(Long id);
    BillingDTO getBillingByNumber(String billingNumber);
    Page<BillingDTO> getAllBillings(Pageable pageable);
    BillingDTO updateBilling(Long id, BillingDTO billingDTO);
    void deleteBilling(Long id);
    Page<BillingDTO> getBillingsByClientId(Long clientId, Pageable pageable);
    Page<BillingDTO> getBillingsByStatus(String status, Pageable pageable);
    List<BillingDTO> getOverdueBillings();
    BillingDTO markAsPaid(Long id, String paymentMethod);
    BillingDTO cancelBilling(Long id);
    List<BillingDTO> getBillingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}