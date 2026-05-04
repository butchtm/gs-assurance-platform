package org.goldstandard.assurance.api.service.account;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AccountCreateUpdate;
import org.goldstandard.assurance.api.dto.AccountFiltersDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final PermissionsService permissionsService;
  private final AccountFinder accountFinder;
  private final UserFinder userFinder;
  private final AccountFactory factory;
  private final AccountTransaction transaction;

  public Page<Account> getAccountsPage(AccountFiltersDTO filters, Pageable pageable) {
    return accountFinder.getAccountsPage(filters, pageable);
  }

  public Account getAccountById(Integer accountId) {
    if (!canViewAccount(accountId)) {
      throw new PermissionException("Not permitted to view account %s", accountId);
    }
    return accountFinder.forceGetAccountById(accountId);
  }

  public int createAccount(AccountCreateUpdate create) {
    Collection<User> users = userFinder.getUsersByIds(create.getUserIds());
    Account account = factory.create(create);
    Account savedAccount = transaction.performCreate(account, users);
    return savedAccount.getId();
  }

  public void updateAccount(Integer accountId, AccountCreateUpdate update) {
    Account account = accountFinder.forceGetAccountById(accountId);
    factory.populateUpdates(account, update);
    Collection<User> originalUsers = userFinder.getUsersByAccountId(account.getId());
    Collection<User> updatedUsers = getActiveUsersById(update.getUserIds());
    transaction.performUpdate(account, originalUsers, updatedUsers);
  }

  public void deleteAccount(Integer accountId) {
    var account = accountFinder.forceGetAccountById(accountId);
    var users = userFinder.getUsersByAccountId(account.getId());
    transaction.performDelete(account, users);
  }

  private boolean canViewAccount(Integer accountId) {
    return permissionsService.isAdminOrArm() || permissionsService.currentUserInAccount(accountId);
  }

  private Collection<User> getActiveUsersById(Collection<Integer> ids) {
    return userFinder.getUsersByIds(ids).stream().filter(User::isActive).toList();
  }
}
