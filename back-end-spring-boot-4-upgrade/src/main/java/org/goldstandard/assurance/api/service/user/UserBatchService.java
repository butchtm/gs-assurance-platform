package org.goldstandard.assurance.api.service.user;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.UserBatchRepository;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserBatchService {

  private final PermissionsService permissionsService;
  private final UserBatchRepository repository;
  private final UserBatchExecutor executor;
  private final Clock clock;

  public UserBatch create(Collection<UpsertUserRequest> requests) {
    permissionsService.validateIsAdmin();

    var now = ZonedDateTime.now(clock);
    var batch =
        UserBatch.builder()
            .count(requests.size())
            .successCount(0)
            .errors(Collections.emptyList())
            .createdAt(now)
            .updatedAt(now)
            .build();

    repository.save(batch);
    executor.execute(batch, requests);
    return batch;
  }

  public UserBatch get(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("user batch %d not found", id));
  }
}
