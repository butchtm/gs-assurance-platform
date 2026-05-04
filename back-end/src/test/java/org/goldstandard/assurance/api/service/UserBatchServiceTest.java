package org.goldstandard.assurance.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.UserBatchRepository;
import org.goldstandard.assurance.api.service.user.UserBatchExecutor;
import org.goldstandard.assurance.api.service.user.UserBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserBatchServiceTest {

  @InjectMocks UserBatchService service;

  @Mock UserBatchRepository repository;

  @Mock UserBatchExecutor executor;

  @Mock PermissionsService permissionsService;

  @Mock Clock clock;

  @Captor ArgumentCaptor<UserBatch> batchCaptor;

  @Captor ArgumentCaptor<Collection<UpsertUserRequest>> requestsCaptor;

  @Nested
  class CreateTests {

    @BeforeEach
    private void mockTime() {
      var now = ZonedDateTime.now();
      when(clock.getZone()).thenReturn(ZoneId.systemDefault());
      when(clock.instant()).thenReturn(now.toInstant());
    }

    @Test
    void shouldCreateBatchInRepository() {
      var requests = buildRequests();

      var batch = service.create(requests);
      assertNotNull(batch);

      verify(repository).save(batchCaptor.capture());
      var savedBatch = batchCaptor.getValue();

      assertEquals(batch, savedBatch);
      assertEquals(requests.size(), savedBatch.getCount());
      assertEquals(0, savedBatch.getSuccessCount());
      assertNotNull(savedBatch.getCreatedAt());
      assertNotNull(savedBatch.getUpdatedAt());
      assertNotNull(savedBatch.getErrors());
      assertEquals(0, savedBatch.getErrors().size());
      assertFalse(savedBatch.isComplete());
    }

    @Test
    void shouldStartExecutor() {
      var requests = buildRequests();

      var batch = service.create(requests);
      assertNotNull(batch);

      verify(executor).execute(batchCaptor.capture(), requestsCaptor.capture());
      var execBatch = batchCaptor.getValue();
      var execRequests = requestsCaptor.getValue();

      assertEquals(batch, execBatch);
      assertEquals(requests, execRequests);

      assertEquals(requests.size(), execBatch.getCount());
      assertEquals(0, execBatch.getSuccessCount());
      assertNotNull(execBatch.getCreatedAt());
      assertNotNull(execBatch.getUpdatedAt());
      assertNotNull(execBatch.getErrors());
      assertEquals(0, execBatch.getErrors().size());
      assertFalse(execBatch.isComplete());
    }

    @Test
    void shouldCheckUserIsAdmin() {
      service.create(buildRequests());
      verify(permissionsService).validateIsAdmin();
    }

    private Collection<UpsertUserRequest> buildRequests() {
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
              .accountId(456)
              .userName("uname2")
              .email("email2")
              .firstName("fname2")
              .lastName("lname2")
              .groups(List.of("g1", "g3"))
              .build());
    }
  }

  @Nested
  class GetTests {
    @Test
    void shouldGetUserBatch() {
      Integer id = 1;
      when(repository.findById(id)).thenReturn(Optional.of(UserBatch.builder().id(id).build()));
      assertNotNull(service.get(id));
    }

    @Test
    void shouldThrowNotFoundExceptionUserBatchDoesNotExist() {
      Integer id = 1;
      when(repository.findById(id)).thenReturn(Optional.empty());
      assertThrows(NotFoundException.class, () -> service.get(id));
    }
  }
}
