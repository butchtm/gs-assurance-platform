package org.goldstandard.assurance.api.service.account;

import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_DELETE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.ACCOUNT_UPDATE;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountTransaction {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final AuditService auditService;

  @Transactional
  public Account performCreate(Account account, Collection<User> users) {
    Account savedAccount = accountRepository.save(account);
    users.forEach(user -> user.setAccount(savedAccount));
    userRepository.saveAll(users);
    auditService.auditEvent(ACCOUNT_CREATE, createAccountAuditInformation(savedAccount.getId()));
    return savedAccount;
  }

  @Transactional
  public void performUpdate(
      Account account, Collection<User> originalUsers, Collection<User> updatedUsers) {
    var usersToAdd = toUsersToAdd(originalUsers, updatedUsers);
    addUsersToAccount(usersToAdd, account);
    var usersToRemove = toUsersToRemove(originalUsers, updatedUsers);
    removeUsersFromAccount(usersToRemove);
    accountRepository.save(account);
    auditService.auditEvent(ACCOUNT_UPDATE, createAccountAuditInformation(account.getId()));
  }

  @Transactional
  public void performDelete(Account account, Collection<User> users) {
    removeUsersFromAccount(users);
    accountRepository.delete(account);
    auditService.auditEvent(ACCOUNT_DELETE, createAccountAuditInformation(account.getId()));
  }

  private void addUsersToAccount(Collection<User> users, Account account) {
    if (users.isEmpty()) {
      return;
    }
    log.info("adding users {} to account {}", toUsernamesString(users), account.getId());
    users.forEach(user -> user.setAccount(account));
    userRepository.saveAll(users);
  }

  private void removeUsersFromAccount(Collection<User> users) {
    if (users.isEmpty()) {
      return;
    }
    log.info("removing account from users {}", toUsernamesString(users));
    users.forEach(user -> user.setAccount(null));
    userRepository.saveAll(users);
  }

  private static Collection<User> toUsersToAdd(
      Collection<User> originalUsers, Collection<User> updatedUsers) {
    var originalIds = toUserIds(originalUsers);
    var updatedIds = toUserIds(updatedUsers);
    var idsToAdd = CollectionUtils.subtract(updatedIds, originalIds);
    return filterByIds(updatedUsers, idsToAdd);
  }

  private static Collection<User> toUsersToRemove(
      Collection<User> originalUsers, Collection<User> updatedUsers) {
    var originalIds = toUserIds(originalUsers);
    var updatedIds = toUserIds(updatedUsers);
    var idsToRemove = CollectionUtils.subtract(originalIds, updatedIds);
    return filterByIds(originalUsers, idsToRemove);
  }

  private static Collection<Integer> toUserIds(Collection<User> users) {
    return users.stream().map(User::getId).toList();
  }

  private static Collection<User> filterByIds(
      Collection<User> users, Collection<Integer> idsToRemove) {
    return users.stream().filter(user -> idsToRemove.contains(user.getId())).toList();
  }

  private static String toUsernamesString(Collection<User> users) {
    return users.stream().map(User::getUserName).collect(Collectors.joining(","));
  }

  private static Map<String, Object> createAccountAuditInformation(Integer accountId) {
    return Map.ofEntries(Map.entry("accountId", accountId));
  }
}
