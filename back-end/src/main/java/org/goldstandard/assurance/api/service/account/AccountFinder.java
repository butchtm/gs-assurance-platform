package org.goldstandard.assurance.api.service.account;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.dto.AccountFiltersDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFinder {

  private final AccountRepository accountRepository;
  private final AccountConfiguration accountConfiguration;
  private final AccountSpecFactory specFactory;

  public List<Account> getByIds(Collection<Integer> ids) {
    var spec = specFactory.toIdInSpec(ids);
    return accountRepository.findAll(spec);
  }

  public Page<Account> getAccountsPage(AccountFiltersDTO filters, Pageable pageable) {
    var spec = specFactory.toSpec(filters);
    return accountRepository.findAll(spec, pageable);
  }

  public Account getArmTeamAccount() {
    return forceGetAccountById(accountConfiguration.getArmTeamAccountId());
  }

  public boolean isVVBAccount(Integer accountId) {
    var account = forceGetAccountById(accountId);
    return account.getAccountType().isVvb();
  }

  public Optional<Account> getAccountById(Integer id) {
    return accountRepository.findById(id);
  }

  public Account forceGetAccountById(Integer accountId) {
    return getAccountById(accountId)
        .orElseThrow(() -> new DataException("Account with id %d not found", accountId));
  }

  public List<Account> getAccountsByAccountTypeId(Integer accountTypeId) {
    return accountRepository.findAllAccountsByAccountType(new AccountType(accountTypeId));
  }
}
