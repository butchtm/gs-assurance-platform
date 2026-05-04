package org.goldstandard.assurance.api.service.user;

import java.time.Clock;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.repository.UserBatchRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBatchExecutor {

  private final UserUpserter upserter;
  private final UserBatchRepository repository;
  private final Clock clock;
  private final ThreadPoolTaskExecutor taskExecutor;

  public void execute(UserBatch batch, Collection<UpsertUserRequest> upsertRequests) {
    Runnable runnable =
        UserBatchRunnable.builder()
            .requests(upsertRequests)
            .upserter(upserter)
            .repository(repository)
            .clock(clock)
            .batch(batch)
            .build();
    taskExecutor.execute(runnable);
  }
}
