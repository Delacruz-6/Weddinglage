package com.weddinglage.service;

import com.weddinglage.domain.AdminAuditLog;
import com.weddinglage.repository.AdminAuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AdminAuditLogRepository repository;

    public AuditService(AdminAuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(String actor, String action, String entityType, String entityId, String details) {
        repository.save(AdminAuditLog.of(actor, action, entityType, entityId, details));
    }
}
