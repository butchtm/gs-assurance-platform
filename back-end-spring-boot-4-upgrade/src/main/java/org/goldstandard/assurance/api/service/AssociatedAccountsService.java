package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssociatedAccountsService {

  private final AccountFinder accountFinder;
  private final AssignmentFinder assignmentFinder;
  private final StakeholderConsultationFinder stakeholderConsultationFinder;
  private final CompetenciesService competenciesService;

  public List<Account> findAccountsAssociatedWithRequest(Request request) throws DataException {
    return findAccountsAssociatedWithRequest(request, Collections.emptyList());
  }

  public List<Account> findAccountsAssociatedWithRequest(
      Request request, Collection<Integer> excludeAccountTypes) throws DataException {
    List<Account> accounts = new ArrayList<>();
    getArmTeamAccount(excludeAccountTypes).ifPresent(accounts::add);
    getProjectDeveloperAccount(request, excludeAccountTypes).ifPresent(accounts::add);
    accounts.addAll(
        assignmentFinder.findAssignedAccountsByRequestId(request.getId(), excludeAccountTypes));
    accounts.addAll(
        getAllRelevantStakeholderAccountsIfInConsultation(request, excludeAccountTypes));
    return accounts;
  }

  public List<Account> getAllRelevantStakeholderAccountsIfInConsultation(
      Request request, Collection<Integer> excludeAccountTypes) {
    if (excludeAccountTypes.contains(TAC_ACCOUNT_TYPE_ID)) {
      return Collections.emptyList();
    }
    return getAllRelevantStakeholderAccountsIfInConsultation(request);
  }

  public List<Account> getAllRelevantStakeholderAccountsIfInConsultation(Request request) {
    if (stakeholderConsultationFinder.isInConsultation(request)) {
      return competenciesService.findAllRelevantStakeholderAccounts(request);
    }
    return Collections.emptyList();
  }

  public List<Account> getInvoiceNotificationAddressees(Request request) {
    // Invoice notifications go to the PD and the VVB (if any)
    var auditAccounts =
        assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()).stream();
    return Stream.concat(Stream.of(getProjectDeveloperAccount(request)), auditAccounts).toList();
  }

  private Optional<Account> getArmTeamAccount(Collection<Integer> excludeAccountTypes) {
    if (excludeAccountTypes.contains(GS_ARM_ACCOUNT_TYPE_ID)) {
      return Optional.empty();
    }
    return Optional.of(accountFinder.getArmTeamAccount());
  }

  private Optional<Account> getProjectDeveloperAccount(
      Request request, Collection<Integer> excludeAccountTypes) {
    if (excludeAccountTypes.contains(PROJECT_DEVELOPER_TYPE_ID)) {
      return Optional.empty();
    }
    return Optional.of(getProjectDeveloperAccount(request));
  }

  private Account getProjectDeveloperAccount(Request request) {
    return accountFinder.forceGetAccountById(request.getProject().getAccountId());
  }
}
