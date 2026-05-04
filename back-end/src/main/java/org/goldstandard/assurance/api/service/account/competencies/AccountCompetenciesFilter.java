package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.CompetencyFilteringNotConfiguredException;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountCompetenciesFilter {

  private final AssignmentFinder assignmentFinder;

  public boolean requestMatchesAccountCompetencies(Request request, Account account) {
    return toPredicate(account).test(request);
  }

  private Predicate<Request> toPredicate(Account account) {
    var typeId = account.getAccountType().getId();
    var competencies = account.getCompetencies();
    return switch (typeId) {
      case TAC_ACCOUNT_TYPE_ID -> new TacCompetenciesPredicate(competencies);
      case VVB_ACCOUNT_TYPE_ID, REVIEWER_ACCOUNT_TYPE_ID ->
          new DefaultAccountCompetenciesPredicate(competencies, assignmentFinder);
      default -> throw new CompetencyFilteringNotConfiguredException(typeId);
    };
  }
}
