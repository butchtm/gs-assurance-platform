package org.goldstandard.assurance.api.service.account.competencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompetenciesDTOValidator {

  private final AccountFinder accountFinder;

  public void validate(CompetenciesDTO competenciesDto, Account account) {
    accountsExistsAndCorrectType(competenciesDto);
    if (account.getAccountType().isTac()) {
      tacCompetenciesValid(competenciesDto);
    }
  }

  private void accountsExistsAndCorrectType(CompetenciesDTO competencies) {
    validateAccountTypes(competencies.getVvbAssignedIds(), AccountType.VVB_ACCOUNT_TYPE_ID);
    validateAccountTypes(
        competencies.getProjectDeveloperIds(), AccountType.PROJECT_DEVELOPER_TYPE_ID);
  }

  private void validateAccountTypes(Collection<Integer> ids, int accountTypeId) {
    if (CollectionUtils.isEmpty(ids)) {
      return;
    }
    var invalidAccountIds = getAccountIdsNotOfType(ids, accountTypeId);
    if (invalidAccountIds.isEmpty()) {
      return;
    }
    throw new CompetenciesException(
        "Supplied account ids %s do not have required account type id %d",
        invalidAccountIds, accountTypeId);
  }

  private Set<Integer> getAccountIdsNotOfType(Collection<Integer> ids, Integer requiredTypeId) {
    return accountFinder.getByIds(ids).stream()
        .filter(a -> !Objects.equals(a.getAccountType().getId(), requiredTypeId))
        .map(Account::getId)
        .collect(Collectors.toSet());
  }

  private static void tacCompetenciesValid(CompetenciesDTO competencies) {
    List<String> invalidCompetencies = new ArrayList<>();
    if (!competencies.getProjectGsids().isEmpty()) {
      invalidCompetencies.add("project gsids");
    }
    if (!competencies.getVvbAssignedIds().isEmpty()) {
      invalidCompetencies.add("assigned vvbs");
    }
    if (!invalidCompetencies.isEmpty()) {
      throw new CompetenciesException(
          "Cannot set competencies [%s] against TAC accounts",
          String.join(", ", invalidCompetencies));
    }
  }
}
