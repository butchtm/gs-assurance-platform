package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountPopulator {

  public static final int NUMBER_OF_BASE_ACCOUNTS = 6;
  public static final ZonedDateTime FIXED_TIME = ZonedDateTime.now();
  public static final AccountType GS_ARM_ACCOUNT_TYPE = new AccountType(GS_ARM_ACCOUNT_TYPE_ID);
  public static final AccountType GS_ACCOUNT_TYPE = new AccountType(GS_ACCOUNT_TYPE_ID);
  public static final AccountType PD_ACCOUNT_TYPE = new AccountType(PROJECT_DEVELOPER_TYPE_ID);
  public static final int TOTAL_ACCOUNTS = NUMBER_OF_BASE_ACCOUNTS + 3;

  public static final int ARM_ACCOUNT_ID = 7;
  public static final int GS_ACCOUNT_ID = 8;
  public static final int PROJECT_DEVELOPER_ACCOUNT_ID = 9;

  @Autowired private AccountRepository accountRepository;

  public boolean isPopulated() {
    return accountRepository.count() > 0;
  }

  public void populate() {
    var accounts = new ArrayList<Account>();
    for (int i = 1; i <= NUMBER_OF_BASE_ACCOUNTS; i++) {
      var type = new AccountType(1 + (i - 1) % 3);
      type.setActive(true);
      accounts.add(createAccount(i, type));
    }
    accounts.add(createAccount(ARM_ACCOUNT_ID, GS_ARM_ACCOUNT_TYPE));
    accounts.add(createAccount(GS_ACCOUNT_ID, GS_ACCOUNT_TYPE));
    accounts.add(createAccount(PROJECT_DEVELOPER_ACCOUNT_ID, PD_ACCOUNT_TYPE));
    var populatedAccounts = accountRepository.saveAll(accounts);
    populatedAccounts.forEach(
        account ->
            log.info(
                "populated account {} with type {}",
                account.getId(),
                account.getAccountType().getId()));
  }

  private Account createAccount(int id, AccountType type) {
    var entity = new Account();
    entity.setName("Account " + id);
    entity.setAddress("Address " + id);
    entity.setEmailAddress("account_" + id + "@test");
    entity.setIsActive(id % 2 == 0);
    entity.setCreatedTime(FIXED_TIME);
    entity.setAccountType(type);
    return entity;
  }
}
