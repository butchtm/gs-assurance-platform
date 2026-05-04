package org.goldstandard.assurance.api.service.process;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.USER_SYNC;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.junit.jupiter.api.Test;

class ProcessRunPolicyTest {

  private static final Duration SYNC_INTERVAL = Duration.ofSeconds(60);
  private static final ZonedDateTime NOW =
      ZonedDateTime.of(
          LocalDate.of(2024, Month.APRIL, 1), LocalTime.of(1, 0), ZoneId.systemDefault());
  private static final ZonedDateTime CUTOFF = NOW.minus(SYNC_INTERVAL);

  private final ProcessRepository repository = mock(ProcessRepository.class);
  private final Clock clock = Clock.fixed(NOW.toInstant(), ZoneId.systemDefault());

  private final ProcessRunPolicy policy =
      new ProcessRunPolicy(repository, clock, SYNC_INTERVAL, USER_SYNC);

  @Test
  void shouldNotRunSyncIfLatestRunIsAtCutoff() {
    givenLatestUserSyncRunAt(CUTOFF);

    boolean result = policy.shouldRun();

    assertThat(result).isFalse();
  }

  @Test
  void shouldRunSyncIfLatestRunWasBeforeCutoff() {
    givenLatestUserSyncRunAt(CUTOFF.minusNanos(1));

    boolean result = policy.shouldRun();

    assertThat(result).isTrue();
  }

  @Test
  void shouldRunSyncIfLatestRunIsNull() {
    givenLatestUserSyncProcess(null);

    boolean result = policy.shouldRun();

    assertThat(result).isTrue();
  }

  @Test
  void shouldNotRunSyncIfLatestRunIsAfterCutoff() {
    givenLatestUserSyncRunAt(CUTOFF.plusNanos(1));

    boolean result = policy.shouldRun();

    assertThat(result).isFalse();
  }

  private void givenLatestUserSyncRunAt(ZonedDateTime timestamp) {
    givenLatestUserSyncProcess(createProcess(timestamp));
  }

  private void givenLatestUserSyncProcess(Process process) {
    when(repository.findLatestForProcessCode(USER_SYNC.name())).thenReturn(process);
  }

  private static Process createProcess(ZonedDateTime statusTimestamp) {
    Process process = new Process();
    process.setProcessCode(USER_SYNC);
    process.setStatusTimestamp(statusTimestamp);
    return process;
  }
}
