package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.CompetenciesConverter;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetenciesService {

  private final AccountFinder accountFinder;
  private final CompetenciesValidator validator;
  private final CompetenciesConverter mapper;
  private final CompetenciesTransaction transaction;
  private final CompetenciesFinder finder;
  private final AccountCompetenciesFilter competenciesFilter;

  public List<Account> findAllRelevantStakeholderAccounts(Request request) {
    return accountFinder.getAccountsByAccountTypeId(TAC_ACCOUNT_TYPE_ID).stream()
        .filter(account -> competenciesFilter.requestMatchesAccountCompetencies(request, account))
        .toList();
  }

  public Integer createCompetencies(Integer accountId, CompetenciesDTO competenciesDto) {
    var account = accountFinder.forceGetAccountById(accountId);
    validator.validateNew(competenciesDto, account);
    Competencies competencies = new Competencies();
    mapper.toEntity(competencies, competenciesDto);
    transaction.create(competencies, account);
    return competencies.getId();
  }

  public Integer updateCompetencies(Integer accountId, CompetenciesDTO competenciesDto) {
    var account = accountFinder.forceGetAccountById(accountId);
    validator.validateUpdate(competenciesDto, account);
    var currentCompetencies = account.getCompetencies();
    mapper.toEntity(currentCompetencies, competenciesDto);
    transaction.update(currentCompetencies, account);
    return currentCompetencies.getId();
  }

  public Optional<CompetenciesDTO> getCompetencyByAccountId(Integer accountId) {
    var competencies = finder.getCompetencyByAccountId(accountId);
    if (competencies.isEmpty()) {
      return Optional.empty();
    }
    var competenciesDto = new CompetenciesDTO();
    mapper.toDto(competencies.get(), competenciesDto, accountId);
    return Optional.of(competenciesDto);
  }
}
