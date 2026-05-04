package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdateId;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalSystemProjectUpdatePersistorTest {

  private static final int MAX_RETRIES = 3;

  @InjectMocks ExternalSystemProjectUpdatePersistor persistor;

  @Mock ExternalUpdateConfig config;

  @Mock ExternalSystemProjectUpdateRepository repository;

  @Mock Clock clock;

  @Test
  void shouldPersistNewFailedAttemptUpdateIfRequiredEvent() {
    var projectId = 1;
    var now = mockTimeNow();
    var expectedSystem = givenExternalSystem();

    persistor.persistUpdateIfRequired(projectId);

    var captor = ArgumentCaptor.forClass(ExternalSystemProjectUpdate.class);
    verify(repository).save(captor.capture());
    var event = captor.getValue();
    assertThat(event.getProjectId()).isEqualTo(projectId);
    assertThat(event.getAttempts()).isOne();
    assertThat(event.getCreatedTimestamp()).isEqualTo(now);
    assertThat(event.getLastAttemptAt()).isEqualTo(now);
    assertThat(event.getDestinationSystem()).isEqualTo(expectedSystem.name());
  }

  @ParameterizedTest
  @ValueSource(ints = {MAX_RETRIES - 1, MAX_RETRIES, MAX_RETRIES + 1})
  void shouldDeleteUpdateIfNoMoreAttemptsRemaining(int attempts) {
    var now = mockTimeNow();
    var system = givenExternalSystem();
    when(config.getMaxRetries()).thenReturn(MAX_RETRIES);
    var update = givenUpdateWithAttempts(attempts);
    var projectId = update.getProjectId();
    when(repository.findById(new ExternalSystemProjectUpdateId(projectId, system.name())))
        .thenReturn(Optional.of(update));

    persistor.persistUpdateIfRequired(projectId);

    ArgumentCaptor<ExternalSystemProjectUpdate> captor =
        ArgumentCaptor.forClass(ExternalSystemProjectUpdate.class);
    verify(repository).delete(captor.capture());
    var savedUpdate = captor.getValue();
    assertThat(savedUpdate)
        .usingRecursiveComparison()
        .ignoringFields("attempts", "lastAttemptAt")
        .isEqualTo(update);
    assertThat(savedUpdate.getAttempts()).isEqualTo(update.getAttempts() + 1);
    assertThat(savedUpdate.getLastAttemptAt()).isEqualTo(now);
  }

  @ParameterizedTest
  @ValueSource(ints = {MAX_RETRIES - 3, MAX_RETRIES - 2})
  void shouldSaveUpdateIfMoreAttemptsRemaining(int attempts) {
    var now = mockTimeNow();
    var system = givenExternalSystem();
    when(config.getMaxRetries()).thenReturn(MAX_RETRIES);
    var update = givenUpdateWithAttempts(attempts);
    var projectId = update.getProjectId();
    when(repository.findById(new ExternalSystemProjectUpdateId(projectId, system.name())))
        .thenReturn(Optional.of(update));

    persistor.persistUpdateIfRequired(update.getProjectId());

    ArgumentCaptor<ExternalSystemProjectUpdate> captor =
        ArgumentCaptor.forClass(ExternalSystemProjectUpdate.class);
    verify(repository).save(captor.capture());
    var savedUpdate = captor.getValue();
    assertThat(savedUpdate)
        .usingRecursiveComparison()
        .ignoringFields("attempts", "lastAttemptAt")
        .isEqualTo(update);
    assertThat(savedUpdate.getAttempts()).isEqualTo(update.getAttempts() + 1);
    assertThat(savedUpdate.getLastAttemptAt()).isEqualTo(now);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private ExternalSystem givenExternalSystem() {
    var system = ExternalSystem.REGISTRY;
    when(config.getSystem()).thenReturn(system);
    return system;
  }

  private ExternalSystemProjectUpdate givenUpdateWithAttempts(int attempts) {
    return ExternalSystemProjectUpdate.builder()
        .projectId(1)
        .createdTimestamp(ZonedDateTime.now())
        .attempts(attempts)
        .destinationSystem(ExternalSystem.REGISTRY.name())
        .build();
  }
}
