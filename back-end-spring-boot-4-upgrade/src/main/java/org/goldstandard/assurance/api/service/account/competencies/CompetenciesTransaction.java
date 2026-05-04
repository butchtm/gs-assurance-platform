package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_COMPETENCIES_CREATED;
import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_COMPETENCIES_UPDATED;

import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.CompetenciesRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetenciesTransaction {

  private final AccountRepository accountRepository;
  private final CompetenciesRepository competenciesRepository;
  private final AuditService auditService;

  @Transactional
  public void create(Competencies competencies, Account account) {
    var savedCompetencies = competenciesRepository.save(competencies);
    account.setCompetencies(savedCompetencies);
    var savedAccount = accountRepository.save(account);
    saveAuditEvent(ACCOUNT_COMPETENCIES_CREATED, savedCompetencies, savedAccount);
  }

  @Transactional
  public void update(Competencies competencies, Account account) {
    var savedCompetencies = competenciesRepository.save(competencies);
    saveAuditEvent(ACCOUNT_COMPETENCIES_UPDATED, savedCompetencies, account);
  }

  private void saveAuditEvent(
      AuditEventSubject subject, Competencies competencies, Account account) {
    var info = toAuditInfo(account.getId(), competencies.getId());
    auditService.auditEvent(subject, info);
  }

  private static Map<String, Object> toAuditInfo(Integer accountId, Integer competenciesId) {
    return Map.ofEntries(
        Map.entry("accountId", accountId), Map.entry("competenciesId", competenciesId));
  }
}
