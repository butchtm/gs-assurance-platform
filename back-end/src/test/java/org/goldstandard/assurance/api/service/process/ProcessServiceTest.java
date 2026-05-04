package org.goldstandard.assurance.api.service.process;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.REGISTRY_UPDATE;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.USER_SYNC;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.COMPLETE;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.FAILED;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.STARTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.entity.system.Process.ProcessCode;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ProcessServiceTest {

  private final ProcessRepository repository = mock(ProcessRepository.class);
  private final Clock clock = mock(Clock.class);

  private final ProcessService service =
      ProcessService.builder().repository(repository).clock(clock).build();

  @Test
  void shouldRecordUserSyncProcessStarted() {
    var now = givenTimeNow();
    service.recordUserSyncProcessStarted();

    var captor = ArgumentCaptor.forClass(Process.class);
    verify(repository).save(captor.capture());
    var process = captor.getValue();
    assertProcessStarted(process, now, USER_SYNC);
  }

  @Test
  void shouldRecordProcessStarted() {
    var now = givenTimeNow();

    service.recordProcessStarted(REGISTRY_UPDATE);

    var captor = ArgumentCaptor.forClass(Process.class);
    verify(repository).save(captor.capture());
    var process = captor.getValue();
    assertProcessStarted(process, now, REGISTRY_UPDATE);
  }

  @Test
  void shouldReturnSavedProcessWhenProcessStarted() {
    givenTimeNow();
    Process savedProcess = givenUserSyncProcess();
    when(repository.save(any(Process.class))).thenReturn(savedProcess);

    var process = service.recordUserSyncProcessStarted();

    assertThat(process).isEqualTo(savedProcess);
  }

  @Test
  void shouldRecordProcessComplete() {
    var now = givenTimeNow();
    var process = givenUserSyncProcess();
    service.recordProcessComplete(process);

    assertThat(process.getStatusTimestamp()).isEqualTo(now);
    assertThat(process.getId()).isNull();
    assertThat(process.getProcessCode()).isEqualTo(USER_SYNC);
    assertThat(process.getProcessStatus()).isEqualTo(COMPLETE);
    assertThat(process.getStartTimestamp()).isNull();
    assertThat(process.getProcessMessage()).isNull();
  }

  @Test
  void shouldRecordProcessFailed() {
    var now = givenTimeNow();
    var process = givenUserSyncProcess();
    var message = "test-error";
    service.recordProcessFailed(process, message);

    assertThat(process.getStatusTimestamp()).isEqualTo(now);
    assertThat(process.getId()).isNull();
    assertThat(process.getProcessCode()).isEqualTo(USER_SYNC);
    assertThat(process.getProcessStatus()).isEqualTo(FAILED);
    assertThat(process.getStartTimestamp()).isNull();
    assertThat(process.getProcessMessage()).isEqualTo(message);
  }

  private ZonedDateTime givenTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private void assertProcessStarted(Process process, ZonedDateTime now, ProcessCode code) {
    assertThat(process.getStartTimestamp()).isEqualTo(now);
    assertThat(process.getStatusTimestamp()).isEqualTo(now);
    assertThat(process.getId()).isNull();
    assertThat(process.getProcessCode()).isEqualTo(code);
    assertThat(process.getProcessStatus()).isEqualTo(STARTED);
    assertThat(process.getProcessMessage()).isNull();
  }

  private Process givenUserSyncProcess() {
    Process process = new Process();
    process.setProcessCode(USER_SYNC);
    return process;
  }
}
