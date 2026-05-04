package org.goldstandard.assurance.api.service.comprehend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprehendJobPollingService {

  private final ProcessService processService;
  private final ComprehendJobRunPolicy runPolicy;
  private final ComprehendJobPoller jobPoller;

  @Scheduled(cron = "${comprehend.poll-for-completion.check-cron}")
  public void runPollingProcessIfRequired() {
    if (!runPolicy.shouldRun()) {
      log.debug("Comprehend polling process not required so not running");
      return;
    }
    runComprehendJobPollingProcess();
  }

  private void runComprehendJobPollingProcess() {
    var process = processService.recordComprehendJobProcessStarted();
    try {
      jobPoller.pollPendingJobsForCompletion();
    } finally {
      processService.recordProcessComplete(process);
    }
  }
}
