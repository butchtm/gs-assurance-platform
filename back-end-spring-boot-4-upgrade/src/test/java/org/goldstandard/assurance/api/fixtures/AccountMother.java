package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.populator.AccountPopulator.ARM_ACCOUNT_ID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMother {

  public static Account any() {
    return tacAccount();
  }

  public static Account withIdOnly(Integer id) {
    return builder().id(id).build();
  }

  public static Account withIdAndName(Integer id, String name) {
    return builder().id(id).name(name).build();
  }

  public static Account armAccount() {
    return Account.builder()
        .id(ARM_ACCOUNT_ID)
        .accountType(new AccountType(AccountType.GS_ARM_ACCOUNT_TYPE_ID))
        .name("GS ARM Team")
        .address("ARM address")
        .emailAddress("arm@caci.co.uk")
        .isActive(true)
        .createdTime(ZonedDateTimeMother.fixed())
        .build();
  }

  public static Account tacAccount() {
    return Account.builder()
        .id(5)
        .accountType(new AccountType(AccountType.TAC_ACCOUNT_TYPE_ID))
        .name("TAC Account")
        .address("TAC address")
        .emailAddress("tac@caci.co.uk")
        .isActive(true)
        .createdTime(ZonedDateTimeMother.fixed())
        .build();
  }

  public static Account.AccountBuilder builder() {
    return Account.builder()
        .id(999)
        .accountType(new AccountType(AccountType.PROJECT_DEVELOPER_TYPE_ID))
        .name("TEST Account")
        .address("TEST address")
        .emailAddress("test-account@caci.co.uk")
        .isActive(true)
        .createdTime(ZonedDateTimeMother.fixed());
  }
}
