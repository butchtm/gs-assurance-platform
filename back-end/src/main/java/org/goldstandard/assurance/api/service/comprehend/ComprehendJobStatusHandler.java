package org.goldstandard.assurance.api.service.comprehend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.JobStatus;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComprehendJobStatusHandler {

  private final ComprehendCompletedJobProcessor completedJobProcessor;

  public void handleJobByStatus(
      DocumentPiiAnalysis pendingJob, PiiEntitiesDetectionJobProperties props) {
    switch (props.jobStatus()) {
      case JobStatus.COMPLETED:
        handleCompleted(pendingJob, props);
        return;
      case JobStatus.IN_PROGRESS, JobStatus.SUBMITTED:
        handleInProgress(props);
        return;
      case JobStatus.FAILED:
        handleFailed(props);
        return;
      default:
        handleUnsupported(props);
    }
  }

  private void handleCompleted(
      DocumentPiiAnalysis pendingJob, PiiEntitiesDetectionJobProperties props) {
    log.info("Job {} has completed. Now processing...", props.jobId());
    completedJobProcessor.processJobOutput(pendingJob.getDocumentId(), props);
  }

  private void handleInProgress(PiiEntitiesDetectionJobProperties props) {
    log.info("Job {} is still in progress", props.jobId());
  }

  private void handleFailed(PiiEntitiesDetectionJobProperties props) {
    throw new DocumentPiiScanException(
        String.format("Job ID %s failed with message %s", props.jobId(), props.message()));
  }

  private void handleUnsupported(PiiEntitiesDetectionJobProperties props) {
    throw new DocumentPiiScanException(
        String.format(
            "Job ID %s had unsupported status %s with message %s",
            props.jobId(), props.jobStatusAsString(), props.message()));
  }
}
