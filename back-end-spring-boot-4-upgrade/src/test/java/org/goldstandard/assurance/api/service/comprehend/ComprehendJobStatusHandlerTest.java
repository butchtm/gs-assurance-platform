package org.goldstandard.assurance.api.service.comprehend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.model.JobStatus;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

@ExtendWith(MockitoExtension.class)
class ComprehendJobStatusHandlerTest {

  @Mock ComprehendCompletedJobProcessor completedJobProcessor;
  @InjectMocks ComprehendJobStatusHandler jobStatusHandler;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();
  private static final String COMPREHEND_JOB_ID = "job123";
  private static final DocumentPiiAnalysis ANALYSIS =
      DocumentPiiAnalysis.builder().documentId(DOCUMENT_ID).build();

  @Test
  void shouldInvokeCompletedJobProcessorToProcessCompletedJob() {
    var completedJob = buildComprehendJob(JobStatus.COMPLETED);

    jobStatusHandler.handleJobByStatus(ANALYSIS, completedJob);

    verify(completedJobProcessor).processJobOutput(DOCUMENT_ID, completedJob);
  }

  @Test
  void shouldDoNothingForInProgressJob() {
    var inProgressJob = buildComprehendJob(JobStatus.IN_PROGRESS);

    assertDoesNotThrow(() -> jobStatusHandler.handleJobByStatus(ANALYSIS, inProgressJob));

    verifyNoInteractions(completedJobProcessor);
  }

  @Test
  void shouldDoNothingForSubmittedJob() {
    var submittedJob = buildComprehendJob(JobStatus.SUBMITTED);

    assertDoesNotThrow(() -> jobStatusHandler.handleJobByStatus(ANALYSIS, submittedJob));

    verifyNoInteractions(completedJobProcessor);
  }

  @Test
  void shouldThrowExceptionForFailedJob() {
    var failedJob = buildComprehendJob(JobStatus.FAILED);

    assertThrows(
        DocumentPiiScanException.class,
        () -> jobStatusHandler.handleJobByStatus(ANALYSIS, failedJob));

    verifyNoInteractions(completedJobProcessor);
  }

  @Test
  void shouldThrowExceptionForJobWithUnsupportedStatus() {
    var unsupportedStatusJob = buildComprehendJob(JobStatus.UNKNOWN_TO_SDK_VERSION);

    assertThrows(
        DocumentPiiScanException.class,
        () -> jobStatusHandler.handleJobByStatus(ANALYSIS, unsupportedStatusJob));

    verifyNoInteractions(completedJobProcessor);
  }

  private PiiEntitiesDetectionJobProperties buildComprehendJob(JobStatus status) {
    return PiiEntitiesDetectionJobProperties.builder()
        .jobId(COMPREHEND_JOB_ID)
        .jobStatus(status)
        .build();
  }
}
