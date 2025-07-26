package org.galymzhan.financetrackerbackend.entity.base;

import jakarta.persistence.Version;

public class BaseEntityAuditVersion extends BaseEntityAudit {
    @Version
    private Long version = 0L;
}
