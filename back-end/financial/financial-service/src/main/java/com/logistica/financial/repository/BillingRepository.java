package com.logistics.financial.repository;

import com.logistics.financial.model.Billing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Optional<Billing> findByBillingNumber(String billingNumber);
    Page<Billing> findByClientId(Long clientId, Pageable pageable);
    Page<Billing> findByStatus(Billing.BillingStatus status, Pageable pageable);
    List<Billing> findByDueDateBefore(LocalDateTime date);
    List<Billing> findByClientIdAndStatus(Long clientId, Billing.BillingStatus status);
    List<Billing> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}