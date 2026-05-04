package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.AuditEventSubject.CONSULTATION_COMPLETE;

import jakarta.transaction.Transactional;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteConsultationTransaction {

  private final StakeholderConsultationRepository repository;
  private final AuditService auditService;
  private final ConsultationAuditInfoFactory auditInfoFactory;

  @Transactional
  public void performComplete(int requestId, Collection<StakeholderConsultation> consultations) {
    var savedConsultations = repository.saveAll(consultations);
    var info = auditInfoFactory.build(requestId, savedConsultations);
    auditService.auditEvent(CONSULTATION_COMPLETE, info);
  }
}
