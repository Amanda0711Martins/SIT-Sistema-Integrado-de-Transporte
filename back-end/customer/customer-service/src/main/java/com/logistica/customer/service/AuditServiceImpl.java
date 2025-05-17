package com.logistica.customer.service;

import com.logistica.customer.model.AuditLog;
import com.logistica.customer.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logEvent(String entityType, Long entityId, String action, String oldValue, String newValue) {
        try {
            AuditLog auditLog = new AuditLog(entityType, entityId, action, oldValue, newValue);
            auditLogRepository.save(auditLog);
            log.info("Audit log created for {} with id {} and action {}", entityType, entityId, action);
        } catch (Exception e) {
            log.error("Error creating audit log for {} with id {} and action {}", entityType, entityId, action, e);
        }
    }
}