package org.goldstandard.assurance.api.exception;

public class CompetencyFilteringNotConfiguredException extends RuntimeException {

  public CompetencyFilteringNotConfiguredException(int accountTypeId) {
    super(
        String.format("Competency predicate not configured for account type id %s", accountTypeId));
  }
}
