package org.goldstandard.assurance.api.populator;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.entity.UserBatchError;
import org.goldstandard.assurance.api.repository.UserBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBatchPopulator {

  @Autowired private UserBatchRepository repository;

  public static final int NUMBER_OF_BATCHES = 12;
  public static final int INCOMPLETE_BATCH_ID = 6;
  public static final int FAILED_BATCH_ID = 8;

  public boolean isPopulated() {
    return repository.count() > 0;
  }

  public void populate() {
    var batches = repository.saveAll(buildUserBatches(NUMBER_OF_BATCHES));
    batches.forEach(
        batch ->
            log.info(
                "create user batch {} with count {}, errors {} and complete {}",
                batch.getId(),
                batch.getCount(),
                batch.getErrors().size(),
                batch.isComplete()));
  }

  private static List<UserBatch> buildUserBatches(int numberOfBatches) {
    return IntStream.range(0, numberOfBatches)
        .map(i -> i + 1)
        .mapToObj(UserBatchPopulator::buildUserBatch)
        .toList();
  }

  private static UserBatch buildUserBatch(int i) {
    var successCount = i;
    if (i == INCOMPLETE_BATCH_ID || i == FAILED_BATCH_ID) {
      successCount--;
    }

    var batch =
        UserBatch.builder()
            .count(i)
            .successCount(successCount)
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .errors(List.of())
            .build();

    if (i == FAILED_BATCH_ID) {
      batch =
          batch.addError(
              UserBatchError.builder()
                  .account("account")
                  .userName("user")
                  .message("an error occurred")
                  .build(),
              ZonedDateTime.now());
    }

    return batch;
  }
}
