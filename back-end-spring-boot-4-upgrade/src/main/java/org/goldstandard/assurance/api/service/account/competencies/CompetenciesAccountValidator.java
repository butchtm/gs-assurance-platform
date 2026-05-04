package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.springframework.stereotype.Service;

@Service
public class CompetenciesAccountValidator {

  private static final List<Integer> VALID_COMPETENCIES_ACCOUNT_TYPES =
      List.of(TAC_ACCOUNT_TYPE_ID, VVB_ACCOUNT_TYPE_ID, REVIEWER_ACCOUNT_TYPE_ID);

  public void validateNew(Account account) {
    accountCompetenciesExistence(account, true);
    competenciesAllowed(account.getAccountType());
  }

  public void validateUpdate(Account account) {
    accountCompetenciesExistence(account, false);
    competenciesAllowed(account.getAccountType());
  }

  private void accountCompetenciesExistence(Account account, boolean isNew) {
    if (!competenciesIsNull(account) && isNew) {
      throw new CompetenciesException(
          "Account id [%d] already has competencies set, cannot create new record.",
          account.getId());
    } else if (competenciesIsNull(account) && !isNew) {
      throw new CompetenciesException(
          "Account id [%d] has no competencies set to update.", account.getId());
    }
  }

  private boolean competenciesIsNull(Account account) {
    return account.getCompetencies() == null;
  }

  private void competenciesAllowed(AccountType accountType) {
    if (!VALID_COMPETENCIES_ACCOUNT_TYPES.contains(accountType.getId())) {
      throw new CompetenciesException(
          "Account type [%s] does not allow competencies to be assigned.",
          accountType.getDescription());
    }
  }
}
