package org.goldstandard.assurance.api.helper;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuditCleanup {
  private final AuditRepository auditRepository;

  public void deleteAllAuditEvents() {
    auditRepository.deleteAll();
  }
}
