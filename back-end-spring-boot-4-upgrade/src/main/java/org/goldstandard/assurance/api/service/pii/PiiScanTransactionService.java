package org.goldstandard.assurance.api.service.pii;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.pii.PiiFinding;
import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.goldstandard.assurance.api.entity.pii.PiiScanState;
import org.goldstandard.assurance.api.entity.pii.ProjectIdPiiScanRun;
import org.goldstandard.assurance.api.enumeration.PiiScanStatus;
import org.goldstandard.assurance.api.enumeration.ProjectScanStatus;
import org.goldstandard.assurance.api.repository.PiiFindingRepository;
import org.goldstandard.assurance.api.repository.PiiScanRunRepository;
import org.goldstandard.assurance.api.repository.PiiScanStateRepository;
import org.goldstandard.assurance.api.repository.ProjectIdPiiScanRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PiiScanTransactionService {

  private final PiiScanRunRepository piiScanRunRepository;
  private final ProjectIdPiiScanRunRepository projectIdPiiScanRunRepository;
  private final PiiFindingRepository piiFindingRepository;
  private final PiiScanStateRepository piiScanStateRepository;
  private final Clock clock;

  @Transactional
  public PiiScanRun saveScanRunWithProjectIds(List<Integer> projectIds) {
    log.info("Saving scan run with {} project IDs", projectIds.size());

    var scanRun = saveScanAsStarted();

    var projectScanRecords =
        projectIds.stream()
            .map(
                projectId ->
                    ProjectIdPiiScanRun.builder()
                        .piiScanRun(scanRun)
                        .projectId(projectId)
                        .status(ProjectScanStatus.NOT_STARTED)
                        .build())
            .toList();

    projectIdPiiScanRunRepository.saveAll(projectScanRecords);

    return scanRun;
  }

  @Transactional
  public void updateScanStatus(PiiScanRun scanRun, PiiScanStatus status) {
    log.info("Updating scan run {} to status: {}", scanRun.getId(), status);

    var updatedRun =
        scanRun.toBuilder().status(status).finishedAt(ZonedDateTime.now(clock)).build();

    updatedRun = calculateAndSetDuration(updatedRun);
    save(updatedRun);
  }

  @Transactional
  public void updateScanStatusWithError(
      PiiScanRun scanRun, PiiScanStatus status, String errorMessage) {
    log.info(
        "Updating scan run {} to status: {} with error: {}", scanRun.getId(), status, errorMessage);

    var updatedRun =
        scanRun.toBuilder()
            .status(status)
            .finishedAt(ZonedDateTime.now(clock))
            .errorMessage(errorMessage)
            .build();

    updatedRun = calculateAndSetDuration(updatedRun);
    save(updatedRun);
  }

  @Transactional
  public void setProjectAnalysisAsRunning(Integer scanRunId, Integer projectId) {
    updateProjectStatus(scanRunId, projectId, ProjectScanStatus.RUNNING);
  }

  @Transactional
  public void setProjectAnalysisAsFinished(
      Collection<PiiFinding> findings, PiiScanRun scanRun, Integer projectId) {
    piiFindingRepository.saveAll(findings);
    updateProjectStatus(scanRun.getId(), projectId, ProjectScanStatus.FINISHED);
    saveLatestProjectState(scanRun, projectId);
  }

  @Transactional
  public void setProjectAnalysisAsFailed(Integer scanRunId, Integer projectId) {
    updateProjectStatus(scanRunId, projectId, ProjectScanStatus.FAILED);
  }

  private PiiScanRun saveScanAsStarted() {
    return save(
        PiiScanRun.builder()
            .startedAt(ZonedDateTime.now(clock))
            .status(PiiScanStatus.STARTED)
            .build());
  }

  private void updateProjectStatus(Integer scanRunId, Integer projectId, ProjectScanStatus status) {
    projectIdPiiScanRunRepository.updateStatusByPiiScanRunAndProjectId(
        scanRunId, projectId, status);
  }

  private PiiScanRun save(PiiScanRun scanRun) {
    return piiScanRunRepository.save(scanRun);
  }

  private PiiScanRun calculateAndSetDuration(PiiScanRun scanRun) {
    if (scanRun.getFinishedAt() != null && scanRun.getStartedAt() != null) {
      var durationMs =
          java.time.Duration.between(scanRun.getStartedAt(), scanRun.getFinishedAt()).toMillis();
      return scanRun.toBuilder().duration(durationMs).build();
    }
    return scanRun;
  }

  private void saveLatestProjectState(PiiScanRun scanRun, Integer projectId) {
    var scanState =
        piiScanStateRepository
            .findByPiiScanRunId(scanRun.getId())
            .map(
                existingState ->
                    existingState.toBuilder()
                        .lastProjectIdScanned(projectId)
                        .updatedAt(ZonedDateTime.now(clock))
                        .build())
            .orElse(
                PiiScanState.builder()
                    .piiScanRun(scanRun)
                    .lastProjectIdScanned(projectId)
                    .updatedAt(ZonedDateTime.now(clock))
                    .build());
    piiScanStateRepository.save(scanState);
  }
}
