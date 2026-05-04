package org.goldstandard.assurance.api.service.comprehend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiDbService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComprehendJobPoller {

  private final DocumentPiiDbService documentPiiDbService;
  private final ComprehendService comprehendService;
  private final ComprehendJobStatusHandler jobStatusHandler;

  public void pollPendingJobsForCompletion() {
    var pendingJobs = documentPiiDbService.getPendingComprehendJobs();
    log.info("Found {} pending comprehend jobs", pendingJobs.size());
    for (var pendingJob : pendingJobs) {
      try {
        checkJobForCompletion(pendingJob);
      } catch (Exception e) {
        log.error(
            "Error checking job with ID {} and document ID {} for completion: {}",
            pendingJob.getComprehendJobId(),
            pendingJob.getDocumentId(),
            e.getMessage(),
            e);
        documentPiiDbService.updateToFailedStatus(pendingJob);
      }
    }
  }

  private void checkJobForCompletion(DocumentPiiAnalysis pendingJob) {
    var detectPiiJob = comprehendService.getDetectPiiJob(pendingJob.getComprehendJobId());
    documentPiiDbService.updateAnalysisComprehendStatus(
        pendingJob, detectPiiJob.jobStatusAsString());
    jobStatusHandler.handleJobByStatus(pendingJob, detectPiiJob);
  }
}
