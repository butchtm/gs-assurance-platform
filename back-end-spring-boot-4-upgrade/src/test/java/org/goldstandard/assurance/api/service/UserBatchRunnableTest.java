package org.goldstandard.assurance.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.exception.UserUpsertException;
import org.goldstandard.assurance.api.repository.UserBatchRepository;
import org.goldstandard.assurance.api.service.user.UserBatchRunnable;
import org.goldstandard.assurance.api.service.user.UserUpserter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserBatchRunnableTest {

  UserBatchRunnable runnable;

  UserBatch batch;

  List<UpsertUserRequest> requests;

  @Mock UserUpserter upserter;

  @Mock UserBatchRepository repository;

  @Mock Clock clock;

  @Captor ArgumentCaptor<UserBatch> batchCaptor;

  @BeforeEach
  void setUp() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());

    batch = buildUserBatch();
    requests = buildRequests();

    runnable =
        UserBatchRunnable.builder()
            .clock(clock)
            .repository(repository)
            .upserter(upserter)
            .batch(batch)
            .requests(requests)
            .build();
  }

  @Test
  void shouldUpdateBatchInRepositoryForEachSuccessfulRequest() {
    runnable.run();

    verify(upserter, times(requests.size())).upsert(any());
    verify(repository, times(requests.size())).save(batchCaptor.capture());
    var savedBatches = batchCaptor.getAllValues();

    assertEquals(requests.size(), savedBatches.size());
    for (var i = 0; i < savedBatches.size(); i++) {
      var savedBatch = savedBatches.get(i);
      assertEquals(0, savedBatch.getErrors().size());
      assertEquals(batch.getId(), savedBatch.getId());
      assertEquals(i + 1, savedBatch.getSuccessCount());
      assertEquals(batch.getCreatedAt(), savedBatch.getCreatedAt());
      assertNotEquals(batch.getUpdatedAt(), savedBatch.getUpdatedAt());
      assertEquals(i == savedBatches.size() - 1, savedBatch.isComplete());
    }
  }

  @Test
  void shouldUpdateBatchInRepositoryForEachFailedRequestIfUpsertFails() {
    doThrow(new UserUpsertException("failed to upsert", "account")).when(upserter).upsert(any());

    runnable.run();

    verify(repository, times(requests.size())).save(batchCaptor.capture());
    var savedBatches = batchCaptor.getAllValues();

    assertEquals(requests.size(), savedBatches.size());
    for (var i = 0; i < savedBatches.size(); i++) {
      var savedBatch = savedBatches.get(i);
      assertEquals(i + 1, savedBatch.getErrors().size());
      assertEquals(batch.getId(), savedBatch.getId());
      assertEquals(0, savedBatch.getSuccessCount());
      assertEquals(batch.getCreatedAt(), savedBatch.getCreatedAt());
      assertNotEquals(batch.getUpdatedAt(), savedBatch.getUpdatedAt());
      assertEquals(i == savedBatches.size() - 1, savedBatch.isComplete());
    }
  }

  private UserBatch buildUserBatch() {
    return UserBatch.builder()
        .id(1)
        .count(2)
        .successCount(0)
        .createdAt(ZonedDateTime.now())
        .updatedAt(ZonedDateTime.now())
        .errors(List.of())
        .build();
  }

  private List<UpsertUserRequest> buildRequests() {
    return List.of(
        UpsertUserRequest.builder()
            .accountId(123)
            .userName("uname1")
            .email("email1")
            .firstName("fname1")
            .lastName("lname1")
            .groups(List.of("g1", "g2"))
            .build(),
        UpsertUserRequest.builder()
            .accountId(123)
            .userName("uname2")
            .email("email2")
            .firstName("fname2")
            .lastName("lname2")
            .groups(List.of("g1", "g3"))
            .build());
  }
}
