package org.goldstandard.assurance.api.service.permission;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.exception.UserAccountNotAssignedException;

public interface AuthenticatedUserSupplier {

  Optional<User> getAuthenticatedUser();

  Set<String> getAuthenticatedUserRoles();

  Optional<String> getAuthenticatedAccessToken();

  default String forceGetAuthenticatedAccessToken() {
    return getAuthenticatedAccessToken()
        .orElseThrow(() -> new PermissionException("user not authenticated"));
  }

  default Integer forceGetAuthenticatedUserPlatformId() {
    return getAuthenticatedUser().map(User::getId).orElse(null);
  }

  default boolean isAuthenticatedUser(UUID userId) {
    return getAuthenticatedUser().map(u -> u.getIdpUserId().equals(userId)).orElse(false);
  }

  default Optional<String> getAuthenticatedUserEmailAddress() {
    return getAuthenticatedUser().map(User::getEmailAddress);
  }

  default Optional<String> getAuthenticatedUsername() {
    return getAuthenticatedUser().map(User::getUserName);
  }

  default String forceGetAuthenticatedUsername() {
    return getAuthenticatedUsername()
        .orElseThrow(() -> new PermissionException("user not authenticated"));
  }

  default User forceGetAuthenticatedUser() {
    return getAuthenticatedUser()
        .orElseThrow(() -> new PermissionException("user not authenticated"));
  }

  default Optional<Account> getAuthenticatedUserAccount() {
    return getAuthenticatedUser()
        .filter(user -> Objects.nonNull(user.getAccount()))
        .map(User::getAccount);
  }

  default Account forceGetAuthenticatedUserAccount() {
    return getAuthenticatedUserAccount().orElseThrow(UserAccountNotAssignedException::new);
  }

  default Integer forceGetAuthenticatedUserAccountId() {
    return getAuthenticatedUserAccountId().orElseThrow(UserAccountNotAssignedException::new);
  }

  default Optional<Integer> getAuthenticatedUserAccountId() {
    return getAuthenticatedUserAccount().map(Account::getId);
  }

  default boolean isAuthenticatedUserAssignedToAnyAccounts(Collection<Integer> accountIds) {
    return getAuthenticatedUserAccountId().filter(accountIds::contains).isPresent();
  }

  default boolean isAuthenticatedUserAssignedToAccount(int accountId) {
    return getAuthenticatedUserAccountId().map(id -> Objects.equals(id, accountId)).orElse(false);
  }
}
