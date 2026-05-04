package org.goldstandard.assurance.api.service.account.competencies;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetenciesFinder {

  private final AccountFinder accountFinder;

  public Optional<Competencies> getCompetencyByAccountId(Integer accountId) {
    return Optional.ofNullable(accountFinder.forceGetAccountById(accountId).getCompetencies());
  }
}
