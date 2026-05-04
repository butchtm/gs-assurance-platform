package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.goldstandard.assurance.api.fixtures.CompetenciesDTOMother;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetenciesDTOValidatorTest {

  @InjectMocks CompetenciesDTOValidator dtoValidator;

  @Mock AccountFinder accountFinder;

  @Test
  void shouldNotThrowExceptionWhenAccountIdsSuppliedOfCorrectType() {
    var dto = CompetenciesDTOMother.preFilled();
    when(accountFinder.getByIds(dto.getVvbAssignedIds())).thenReturn(List.of(buildVVBAccount()));
    when(accountFinder.getByIds(dto.getProjectDeveloperIds()))
        .thenReturn(List.of(buildPDAccount()));

    ThrowingCallable call = () -> dtoValidator.validate(dto, buildAnyAccount());

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionWhenAccountIdsSuppliedAreEmpty() {
    var dto = CompetenciesDTOMother.empty();

    ThrowingCallable call = () -> dtoValidator.validate(dto, buildAnyAccount());

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenAccountIdsSuppliedOfWrongType() {
    var dto = CompetenciesDTOMother.preFilled();
    when(accountFinder.getByIds(dto.getVvbAssignedIds())).thenReturn(List.of(buildPDAccount()));

    Throwable error = catchThrowable(() -> dtoValidator.validate(dto, buildAnyAccount()));

    assertThat(error)
        .isInstanceOf(CompetenciesException.class)
        .hasMessage("Supplied account ids [2] do not have required account type id 1");
  }

  @Test
  void shouldThrowExceptionWhenTacAssignedInvalidValues() {
    var dto = CompetenciesDTOMother.preFilled();
    when(accountFinder.getByIds(dto.getVvbAssignedIds())).thenReturn(List.of(buildVVBAccount()));
    when(accountFinder.getByIds(dto.getProjectDeveloperIds()))
        .thenReturn(List.of(buildPDAccount()));

    Throwable error = catchThrowable(() -> dtoValidator.validate(dto, buildTACAccount()));

    assertThat(error)
        .isInstanceOf(CompetenciesException.class)
        .hasMessage("Cannot set competencies [project gsids, assigned vvbs] against TAC accounts");
  }

  private static Account buildAnyAccount() {
    return buildAccount(0, 0);
  }

  private static Account buildVVBAccount() {
    return buildAccount(1, AccountType.VVB_ACCOUNT_TYPE_ID);
  }

  private static Account buildPDAccount() {
    return buildAccount(2, AccountType.PROJECT_DEVELOPER_TYPE_ID);
  }

  private static Account buildTACAccount() {
    return buildAccount(3, AccountType.TAC_ACCOUNT_TYPE_ID);
  }

  private static Account buildAccount(int id, int typeId) {
    var account = new Account(id);
    account.setAccountType(new AccountType(typeId));
    return account;
  }
}
