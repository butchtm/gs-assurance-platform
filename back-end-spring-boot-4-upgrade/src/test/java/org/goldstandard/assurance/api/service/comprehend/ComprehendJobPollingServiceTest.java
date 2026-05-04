package org.goldstandard.assurance.api.service.comprehend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComprehendJobPollingServiceTest {

  @InjectMocks private ComprehendJobPollingService jobService;
  @Mock private ProcessService processService;
  @Mock private ComprehendJobRunPolicy runPolicy;
  @Mock private ComprehendJobPoller jobPoller;

  @Test
  void shouldRunPollerIfRequired() {
    var comprehendProcess = mock(Process.class);
    when(runPolicy.shouldRun()).thenReturn(true);
    when(processService.recordComprehendJobProcessStarted()).thenReturn(comprehendProcess);

    jobService.runPollingProcessIfRequired();

    verify(runPolicy).shouldRun();
    verify(processService).recordComprehendJobProcessStarted();
    verify(jobPoller).pollPendingJobsForCompletion();
    verify(processService).recordProcessComplete(comprehendProcess);
  }

  @Test
  void shouldNotRunPollerIfNotRequired() {
    when(runPolicy.shouldRun()).thenReturn(false);

    jobService.runPollingProcessIfRequired();

    verify(runPolicy).shouldRun();
    verifyNoInteractions(processService, jobPoller);
  }
}
