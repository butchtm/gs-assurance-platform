package org.goldstandard.assurance.api.service.pii;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.List;
import org.goldstandard.assurance.api.dto.ProjectIdsDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.goldstandard.assurance.api.enumeration.PiiScanStatus;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PiiScanAsyncProcessorFailoverTest {

  @Mock private PiiScanTransactionService transactionService;
  @Mock private ProjectFinder projectFinder;
  @Mock private ComprehendService comprehendService;
  @Mock private Clock clock;

  @InjectMocks private PiiScanAsyncProcessor processor;

  private PiiScanRun scanRun;
  private ProjectIdsDTO projectIdsDTO;
  private Project project;

  @BeforeEach
  void setUp() {
    scanRun = PiiScanRun.builder().id(1).build();
    projectIdsDTO = ProjectIdsDTO.builder().projectIds(List.of(100, 200, 300)).build();
    project =
        Project.builder()
            .id(100)
            .name("Test Project")
            .description("Test Description")
            .notes("Test Notes")
            .build();
  }

  @Test
  void shouldHandleFailureWhenUpdateScanStatusThrowsException() {
    String errorMessage = "Database connection failed";
    doThrow(new RuntimeException(errorMessage))
        .when(transactionService)
        .updateScanStatus(scanRun, PiiScanStatus.RUNNING);

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.RUNNING);
    verify(transactionService)
        .updateScanStatusWithError(scanRun, PiiScanStatus.FAILED, errorMessage);
    verify(projectFinder, never()).forceGetProjectById(anyInt());
  }

  @Test
  void shouldHandleFailureWhenProjectFinderThrowsException() {
    String errorMessage = "Project not found";
    when(projectFinder.forceGetProjectById(100)).thenThrow(new RuntimeException(errorMessage));

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.RUNNING);
    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(projectFinder).forceGetProjectById(200);
    verify(projectFinder).forceGetProjectById(300);
  }

  @Test
  void shouldHandleFailureWhenComprehendServiceThrowsException() {
    String errorMessage = "AWS Comprehend service unavailable";
    when(projectFinder.forceGetProjectById(anyInt())).thenReturn(project);
    when(comprehendService.detectEntities(anyList())).thenThrow(new RuntimeException(errorMessage));

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.RUNNING);
    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(transactionService).setProjectAnalysisAsRunning(1, 200);
    verify(transactionService).setProjectAnalysisAsFailed(1, 200);
    verify(transactionService).setProjectAnalysisAsRunning(1, 300);
    verify(transactionService).setProjectAnalysisAsFailed(1, 300);
  }

  @Test
  void shouldHandleFailureWhenSetProjectAnalysisAsRunningThrowsException() {
    String errorMessage = "Transaction failed";
    doThrow(new RuntimeException(errorMessage))
        .when(transactionService)
        .setProjectAnalysisAsRunning(1, 100);

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.RUNNING);
    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(projectFinder, never()).forceGetProjectById(100);
    verify(projectFinder).forceGetProjectById(200);
    verify(projectFinder).forceGetProjectById(300);
  }

  @Test
  void shouldHandleFailureWhenSetProjectAnalysisAsFinishedThrowsException() {
    String errorMessage = "Save findings failed";
    when(projectFinder.forceGetProjectById(anyInt())).thenReturn(project);
    when(comprehendService.detectEntities(anyList())).thenReturn(List.of());
    doThrow(new RuntimeException(errorMessage))
        .when(transactionService)
        .setProjectAnalysisAsFinished(anyList(), eq(scanRun), eq(100));

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(projectFinder).forceGetProjectById(100);
    verify(comprehendService, times(3)).detectEntities(anyList());
    verify(transactionService).setProjectAnalysisAsFinished(anyList(), eq(scanRun), eq(100));
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(projectFinder).forceGetProjectById(200);
    verify(projectFinder).forceGetProjectById(300);
  }

  @Test
  void shouldContinueProcessingAfterIndividualProjectFailures() {
    when(projectFinder.forceGetProjectById(100))
        .thenThrow(new RuntimeException("Project 100 failed"));
    when(projectFinder.forceGetProjectById(200)).thenReturn(project);
    when(projectFinder.forceGetProjectById(300))
        .thenThrow(new RuntimeException("Project 300 failed"));
    when(comprehendService.detectEntities(anyList())).thenReturn(List.of());

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(transactionService).setProjectAnalysisAsRunning(1, 200);
    verify(transactionService).setProjectAnalysisAsFinished(anyList(), eq(scanRun), eq(200));
    verify(transactionService).setProjectAnalysisAsRunning(1, 300);
    verify(transactionService).setProjectAnalysisAsFailed(1, 300);
    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.COMPLETED);
    verify(transactionService, never())
        .updateScanStatusWithError(
            any(PiiScanRun.class), any(PiiScanStatus.class), any(String.class));
  }

  @Test
  void shouldHandleMultipleConsecutiveFailures() {
    when(projectFinder.forceGetProjectById(anyInt()))
        .thenThrow(new RuntimeException("Service completely down"));

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(transactionService).setProjectAnalysisAsRunning(1, 200);
    verify(transactionService).setProjectAnalysisAsFailed(1, 200);
    verify(transactionService).setProjectAnalysisAsRunning(1, 300);
    verify(transactionService).setProjectAnalysisAsFailed(1, 300);
    verify(transactionService).updateScanStatus(scanRun, PiiScanStatus.COMPLETED);
  }

  @Test
  void shouldHandleNullPointerExceptionInProjectProcessing() {
    when(projectFinder.forceGetProjectById(100)).thenReturn(null);

    processor.performPiiScanAsync(scanRun, projectIdsDTO);

    verify(transactionService).setProjectAnalysisAsRunning(1, 100);
    verify(transactionService).setProjectAnalysisAsFailed(1, 100);
    verify(projectFinder).forceGetProjectById(200);
    verify(projectFinder).forceGetProjectById(300);
  }
}
