package org.goldstandard.assurance.api.helper;

import static org.goldstandard.assurance.api.populator.AccountPopulator.ARM_ACCOUNT_ID;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

@TestConfiguration
@Component
public class AccountHelper {

  @Autowired private AccountRepository accountRepository;

  public Account createTestAccountWithType(int accountTypeId) {
    var count = accountRepository.count() + 1;
    var account = new Account();
    account.setName(String.format("test account %d", count));
    account.setAccountType(new AccountType(accountTypeId));
    accountRepository.save(account);
    return account;
  }

  public Account getArmAccount() {
    return accountRepository.findById(ARM_ACCOUNT_ID).orElseThrow();
  }

  public void deleteTestAccounts(Account... accounts) {
    Arrays.stream(accounts).forEach(this::deleteTestAccount);
  }

  @Transactional
  public void deleteTestAccount(Account account) {
    accountRepository.delete(account);
  }
}
