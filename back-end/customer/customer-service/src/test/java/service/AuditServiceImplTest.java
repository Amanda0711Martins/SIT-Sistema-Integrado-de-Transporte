package service;

import com.logistica.customer.model.AuditLog;
import com.logistica.customer.repository.AuditLogRepository;
import com.logistica.customer.service.AuditServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    void testLogEvent_shouldSaveAuditLog() {
        // Arrange
        String entityType = "Customer";
        Long entityId = 1L;
        String action = "CREATE";
        String oldValue = "null";
        String newValue = "{\"name\":\"Test Customer\"}";

        // Act
        auditService.logEvent(entityType, entityId, action, oldValue, newValue);

        // Assert
        // Verifica se o método save do repositório foi chamado exatamente uma vez com qualquer objeto AuditLog
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
