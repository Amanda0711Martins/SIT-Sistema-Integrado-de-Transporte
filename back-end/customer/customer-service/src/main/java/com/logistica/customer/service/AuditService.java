package com.logistica.customer.service;

public interface AuditService {
    void logEvent(String entityType, Long entityId, String action, String oldValue, String newValue);
}