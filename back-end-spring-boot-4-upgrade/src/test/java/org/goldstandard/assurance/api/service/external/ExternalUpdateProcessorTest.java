package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class ExternalUpdateProcessorTest {

  @InjectMocks ExternalUpdateProcessor processor;

  @Mock Process.ProcessCode processCode;

  @Mock ProcessRunPolicy runPolicy;

  @Mock ProcessService processService;

  @Mock ExternalUpdateFinder finder;

  @Mock ExternalUpdateFailureHandler failureHandler;

  @Mock ExternalUpdater externalUpdater;

  @Mock Process process;

  @Captor ArgumentCaptor<Integer> projectIdCaptor;

  @Test
  void shouldNotRunIfNotRequired() {
    when(runPolicy.shouldRun()).thenReturn(false);

    processor.processUpdatesIfRequired();

    verifyNoInteractions(processService, finder, externalUpdater, failureHandler);
  }

  @Test
  void shouldAbortIfRequired() {
    when(runPolicy.shouldRun()).thenReturn(true);
    when(processService.recordProcessStarted(processCode)).thenReturn(process);

    var u1 = buildUpdate(1);
    var u2 = buildUpdate(2);
    givenPageOfUpdatesReturned(u1, u2);

    var apiError = new ExternalApiClientException("test-error");
    doThrow(apiError).when(externalUpdater).update(1);

    var abortEx = new ExternalUpdateAbortException(ExternalSystem.REGISTRY, apiError);
    doThrow(abortEx).when(failureHandler).abortIfRequired(apiError);

    processor.processUpdatesIfRequired();

    verify(externalUpdater, times(1)).update(1);
    verify(externalUpdater, never()).update(2);
    verify(processService).recordProcessFailed(process, abortEx.getMessage());
    verify(processService, never()).recordProcessComplete(any());
  }

  @Test
  void shouldProcessAllUpdatesIfNoErrorsThrown() {
    when(runPolicy.shouldRun()).thenReturn(true);
    when(processService.recordProcessStarted(processCode)).thenReturn(process);
    var u1 = buildUpdate(1);
    var u2 = buildUpdate(2);
    givenPageOfUpdatesReturned(u1, u2);

    processor.processUpdatesIfRequired();

    verify(externalUpdater, times(2)).update(projectIdCaptor.capture());
    assertThat(projectIdCaptor.getAllValues()).containsExactly(1, 2);
    verify(processService).recordProcessComplete(process);
  }

  private void givenPageOfUpdatesReturned(ExternalSystemProjectUpdate... updates) {
    when(finder.getPageOfUpdates()).thenReturn(new PageImpl<>(List.of(updates)));
  }

  private static ExternalSystemProjectUpdate buildUpdate(int projectId) {
    return ExternalSystemProjectUpdate.builder().projectId(projectId).build();
  }
}
