package org.goldstandard.assurance.api.service.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.InvalidUserGroupException;
import org.goldstandard.assurance.api.exception.UserAlreadyExistsException;
import org.goldstandard.assurance.api.exception.UserUpsertException;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpserter {

  private static final String COGNITO_FAILED_MESSAGE = "failed to update user in cognito";

  private final AccountFinder accountFinder;
  private final UserFinder userFinder;
  private final CognitoUserService cognitoUserService;
  private final UserRepository repository;

  public void upsert(UpsertUserRequest request) {
    try {
      var userName = request.getUserName();
      log.info("upserting user {}", userName);
      Optional<User> user = userFinder.getByUserName(userName);
      if (user.isPresent()) {
        update(user.get(), request);
      } else {
        create(request);
      }
    } catch (DataException e) {
      throw new UserUpsertException("Cannot find platform account - please check account ID", e);
    }
  }

  private void update(User existingUser, UpsertUserRequest request) {
    var username = request.getUserName();
    var account = getAccountForIdIfPresent(request);
    var accountName = account.map(Account::getName).orElse("");
    if (!cognitoUserService.exists(username)) {
      throw new UserUpsertException("Existing user not found in Cognito", accountName);
    }
    try {
      existingUser.update(request);
      account.ifPresentOrElse(existingUser::setAccount, () -> existingUser.setAccount(null));
      cognitoUserService.update(existingUser);
      repository.save(existingUser);
    } catch (InvalidUserGroupException e) {
      log.warn(COGNITO_FAILED_MESSAGE, e);
      throw new UserUpsertException(
          "Failed to update user in Cognito - invalid group", accountName, e);
    } catch (SdkException e) {
      log.warn(COGNITO_FAILED_MESSAGE, e);
      throw new UserUpsertException(
          "Failed to update user in Cognito - please retry", accountName, e);
    } catch (DataAccessException e) {
      log.warn("failed to update user in repository", e);
      throw new UserUpsertException(
          "Failed to update user in database - please wait for user sync", accountName, e);
    }
  }

  private void create(UpsertUserRequest request) {
    var account = getAccountForIdIfPresent(request);
    var accountName = account.map(Account::getName).orElse("");
    try {
      var user = cognitoUserService.create(request);
      account.ifPresent(user::setAccount);
      user.setEmailVerified(true);
      user.setIsActive(true);
      repository.save(user);
    } catch (InvalidUserGroupException e) {
      log.warn(COGNITO_FAILED_MESSAGE, e);
      throw new UserUpsertException(
          "Failed to update user in Cognito - invalid group", accountName, e);
    } catch (SdkException e) {
      log.warn("failed to create user in cognito", e);
      throw new UserUpsertException(
          "Failed to create user in Cognito - please retry", accountName, e);
    } catch (UserAlreadyExistsException e) {
      log.warn("failed to create user in cognito", e);
      throw new UserUpsertException(
          "Failed to create user in Cognito - already exists", accountName, e);
    } catch (DataAccessException e) {
      log.warn("failed to create user in repository", e);
      throw new UserUpsertException(
          "Failed to create user in database - please wait for user sync", accountName, e);
    }
  }

  private Optional<Account> getAccountForIdIfPresent(UpsertUserRequest request) {
    var accountId = request.getAccountId();
    return accountId.map(accountFinder::forceGetAccountById);
  }
}
