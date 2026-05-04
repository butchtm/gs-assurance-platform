package org.goldstandard.assurance.api.service.process;

import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.COMPLETE;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.FAILED;
import static org.goldstandard.assurance.api.entity.system.Process.ProcessStatus.STARTED;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.entity.system.Process.ProcessCode;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class ProcessService {

  private final ProcessRepository repository;
  private final Clock clock;

  public Process recordUserSyncProcessStarted() {
    return recordProcessStarted(ProcessCode.USER_SYNC);
  }

  public Process recordComprehendJobProcessStarted() {
    return recordProcessStarted(ProcessCode.PII_SCANNING);
  }

  public Process recordConsultationNotificationJobProcessStart() {
    return recordProcessStarted(ProcessCode.CONSULTATION_NOTIFICATION);
  }

  public Process recordConversationNotificationJobProcessStart() {
    return recordProcessStarted(ProcessCode.CONVERSATION_NOTIFICATION);
  }

  public void recordProcessComplete(Process process) {
    log.info("{} process complete", process.getProcessCode());
    process.setProcessStatus(COMPLETE);
    recordProcessFinished(process);
  }

  public void recordProcessFailed(Process process, String message) {
    log.error("{} process failed", process.getProcessCode());
    process.setProcessStatus(FAILED);
    process.setProcessMessage(message);
    recordProcessFinished(process);
  }

  public Process recordProcessStarted(ProcessCode code) {
    log.info("starting {} process", code);
    var now = ZonedDateTime.now(clock);
    var process = new Process();
    process.setProcessCode(code);
    process.setProcessStatus(STARTED);
    process.setStartTimestamp(now);
    process.setStatusTimestamp(now);
    return repository.save(process);
  }

  private void recordProcessFinished(Process process) {
    process.setStatusTimestamp(ZonedDateTime.now(clock));
    repository.save(process);
  }
}
