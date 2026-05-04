package org.goldstandard.assurance.api.service.user;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.entity.UserBatchError;
import org.goldstandard.assurance.api.exception.UserUpsertException;
import org.goldstandard.assurance.api.repository.UserBatchRepository;

@Builder
@Slf4j
public class UserBatchRunnable implements Runnable {

  private final UserBatch batch;
  private final UserUpserter upserter;
  private final UserBatchRepository repository;
  private final Clock clock;
  private final Collection<UpsertUserRequest> requests;

  @Override
  public void run() {
    UserBatch updatedBatch = batch;
    log.info(
        "running update for user batch {} on thread {}",
        batch.getId(),
        Thread.currentThread().getName());
    for (UpsertUserRequest request : requests) {
      updatedBatch = updateBatch(updatedBatch, request);
      repository.save(updatedBatch);
    }
  }

  private UserBatch updateBatch(UserBatch userBatch, UpsertUserRequest request) {
    ZonedDateTime now = ZonedDateTime.now(clock);
    try {
      upserter.upsert(request);
      return userBatch.addSuccessfulUser(now);
    } catch (UserUpsertException e) {
      log.warn("failed to upsert user {}", request.getUserName(), e);
      return userBatch.addError(toError(request, e.getMessage(), e.getAccountName()), now);
    } catch (Exception e) {
      log.warn("failed to upsert user {}", request.getUserName(), e);
      return userBatch.addError(toError(request, e.getMessage()), now);
    }
  }

  private static UserBatchError toError(UpsertUserRequest request, String errorMessage) {
    return toErrorBuilder(request, errorMessage).build();
  }

  private static UserBatchError toError(
      UpsertUserRequest request, String errorMessage, String accountName) {
    return toErrorBuilder(request, errorMessage).account(accountName).build();
  }

  private static UserBatchError.UserBatchErrorBuilder toErrorBuilder(
      UpsertUserRequest request, String errorMessage) {
    return UserBatchError.builder().userName(request.getUserName()).message(errorMessage);
  }
}
