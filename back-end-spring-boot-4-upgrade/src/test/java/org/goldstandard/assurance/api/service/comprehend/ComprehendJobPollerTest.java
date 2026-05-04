package org.goldstandard.assurance.api.service.comprehend;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiDbService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.model.JobStatus;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

@ExtendWith(MockitoExtension.class)
class ComprehendJobPollerTest {

  @InjectMocks private ComprehendJobPoller jobPoller;
  @Mock DocumentPiiDbService documentPiiDbService;
  @Mock ComprehendService comprehendService;
  @Mock ComprehendJobStatusHandler jobStatusHandler;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();
  private static final String COMPREHEND_JOB_ID = "job123";

  @Test
  void shouldInvokeStatusHandlerToProcessPendingJob() {
    var pendingJob =
        DocumentPiiAnalysis.builder()
            .documentId(DOCUMENT_ID)
            .comprehendJobId(COMPREHEND_JOB_ID)
            .build();
    var comprehendJob =
        PiiEntitiesDetectionJobProperties.builder().jobStatus(JobStatus.COMPLETED).build();
    when(documentPiiDbService.getPendingComprehendJobs()).thenReturn(List.of(pendingJob));
    when(comprehendService.getDetectPiiJob(pendingJob.getComprehendJobId()))
        .thenReturn(comprehendJob);

    jobPoller.pollPendingJobsForCompletion();

    verify(documentPiiDbService).getPendingComprehendJobs();
    verify(comprehendService).getDetectPiiJob(COMPREHEND_JOB_ID);
    verify(documentPiiDbService)
        .updateAnalysisComprehendStatus(pendingJob, JobStatus.COMPLETED.toString());
    verify(jobStatusHandler).handleJobByStatus(pendingJob, comprehendJob);
  }

  @Test
  void shouldUpdateAnalysisToFailedStatusIfExceptionThrown() {
    var pendingJob =
        DocumentPiiAnalysis.builder()
            .documentId(DOCUMENT_ID)
            .comprehendJobId(COMPREHEND_JOB_ID)
            .build();
    var comprehendJob =
        PiiEntitiesDetectionJobProperties.builder().jobStatus(JobStatus.COMPLETED).build();
    when(documentPiiDbService.getPendingComprehendJobs()).thenReturn(List.of(pendingJob));
    when(comprehendService.getDetectPiiJob(pendingJob.getComprehendJobId()))
        .thenReturn(comprehendJob);
    doThrow(DocumentPiiScanException.class)
        .when(jobStatusHandler)
        .handleJobByStatus(pendingJob, comprehendJob);

    jobPoller.pollPendingJobsForCompletion();

    verify(documentPiiDbService).getPendingComprehendJobs();
    verify(comprehendService).getDetectPiiJob(COMPREHEND_JOB_ID);
    verify(documentPiiDbService)
        .updateAnalysisComprehendStatus(pendingJob, JobStatus.COMPLETED.toString());
    verify(jobStatusHandler).handleJobByStatus(pendingJob, comprehendJob);
    verify(documentPiiDbService).updateToFailedStatus(pendingJob);
  }
}
