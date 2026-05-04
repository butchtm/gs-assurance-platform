package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_COMPETENCIES_CREATED;
import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_COMPETENCIES_UPDATED;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.CompetenciesRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetenciesTransactionTest {

  @InjectMocks CompetenciesTransaction transaction;

  @Mock CompetenciesRepository competenciesRepository;

  @Mock AccountRepository accountRepository;

  @Mock AuditService auditService;

  @Captor ArgumentCaptor<Account> accountCaptor;

  @Captor ArgumentCaptor<Competencies> competenciesCaptor;

  @Captor ArgumentCaptor<Map<String, Object>> auditInfoCaptor;

  @Test
  void shouldSetCompetenciesAgainstAccountBeforeSaveOnCreate() {
    var competencies = buildCompetencies();
    var savedCompetencies = givenSavedCompetencies(competencies);
    var account = buildAccount();
    givenSavedAccount(account);

    transaction.create(competencies, account);

    verify(accountRepository).save(accountCaptor.capture());
    var savedAccount = accountCaptor.getValue();
    assertThat(savedAccount).isEqualTo(account);
    assertThat(savedAccount.getCompetencies()).isEqualTo(savedCompetencies);
  }

  @Test
  void shouldAuditCompetenciesCreated() {
    var competencies = buildCompetencies();
    var savedCompetencies = givenSavedCompetencies(competencies);
    var account = buildAccount();
    var savedAccount = givenSavedAccount(account);

    transaction.create(competencies, account);

    verify(auditService).auditEvent(eq(ACCOUNT_COMPETENCIES_CREATED), auditInfoCaptor.capture());
    var auditInfo = auditInfoCaptor.getValue();
    assertThat(auditInfo)
        .hasSize(2)
        .contains(
            entry("accountId", savedAccount.getId()),
            entry("competenciesId", savedCompetencies.getId()));
  }

  @Test
  void shouldSaveCompetenciesOnUpdate() {
    var competencies = buildCompetencies();
    givenSavedCompetencies(competencies);
    var account = buildAccount();

    transaction.update(competencies, account);

    verify(competenciesRepository).save(competenciesCaptor.capture());
    var savedCompetencies = competenciesCaptor.getValue();
    assertThat(savedCompetencies).isEqualTo(competencies);
  }

  @Test
  void shouldAuditCompetenciesUpdated() {
    var competencies = buildCompetencies();
    var savedCompetencies = givenSavedCompetencies(competencies);
    var account = buildAccount();

    transaction.update(competencies, account);

    verify(auditService).auditEvent(eq(ACCOUNT_COMPETENCIES_UPDATED), auditInfoCaptor.capture());
    var auditInfo = auditInfoCaptor.getValue();
    assertThat(auditInfo)
        .hasSize(2)
        .contains(
            entry("accountId", account.getId()),
            entry("competenciesId", savedCompetencies.getId()));
  }

  private Competencies givenSavedCompetencies(Competencies competencies) {
    var savedCompetencies = mock(Competencies.class);
    when(savedCompetencies.getId()).thenReturn(competencies.getId());
    when(competenciesRepository.save(competencies)).thenReturn(savedCompetencies);
    return savedCompetencies;
  }

  private Account givenSavedAccount(Account account) {
    var savedAccount = mock(Account.class);
    when(savedAccount.getId()).thenReturn(account.getId());
    when(accountRepository.save(account)).thenReturn(savedAccount);
    return savedAccount;
  }

  private static Account buildAccount() {
    return new Account(0);
  }

  private static Competencies buildCompetencies() {
    return Competencies.builder()
        .id(1)
        .estimatedAnnualEmissionReductionAmount(2)
        .projectGsids(List.of("GS1"))
        .build();
  }
}
