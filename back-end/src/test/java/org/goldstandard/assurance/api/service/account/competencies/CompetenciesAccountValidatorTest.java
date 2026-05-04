package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_FINANCE_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetenciesAccountValidatorTest {

  @InjectMocks CompetenciesAccountValidator accountValidator;

  @ParameterizedTest
  @ValueSource(ints = {TAC_ACCOUNT_TYPE_ID, VVB_ACCOUNT_TYPE_ID, REVIEWER_ACCOUNT_TYPE_ID})
  void shouldNotThrowExceptionForValidAccountTypes(Integer accountTypeId) {
    var account = createAccount(null, new AccountType(accountTypeId));
    assertDoesNotThrow(() -> accountValidator.validateNew(account));
  }

  @ParameterizedTest
  @ValueSource(
      ints = {
        GS_ACCOUNT_TYPE_ID,
        PROJECT_DEVELOPER_TYPE_ID,
        GS_ARM_ACCOUNT_TYPE_ID,
        GS_FINANCE_ACCOUNT_TYPE_ID
      })
  void shouldThrowExceptionForInvalidAccountTypes(Integer accountTypeId) {
    var account = createAccount(null, new AccountType(accountTypeId));
    assertThrows(CompetenciesException.class, () -> accountValidator.validateNew(account));
  }

  @Test
  void shouldThrowExceptionIfCompetenciesExistForNewRecord() {
    var account = createAccount(new Competencies(), new AccountType(0));
    assertThrows(CompetenciesException.class, () -> accountValidator.validateNew(account));
  }

  @Test
  void shouldThrowExceptionIfCompetenciesDoNotExistForUpdate() {
    var account = createAccount(null, new AccountType(0));
    assertThrows(CompetenciesException.class, () -> accountValidator.validateUpdate(account));
  }

  @Test
  void shouldNotThrowExceptionIfCompetenciesExistForUpdate() {
    var account = createAccount(new Competencies(), new AccountType(0));
    assertDoesNotThrow(() -> accountValidator.validateUpdate(account));
  }

  private Account createAccount(Competencies competencies, AccountType accountType) {
    Account account = new Account();
    account.setAccountType(accountType);
    account.setCompetencies(competencies);
    return account;
  }
}
