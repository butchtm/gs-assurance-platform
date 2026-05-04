package org.goldstandard.assurance.api.service.pii;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.ProjectIdsDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.pii.PiiFinding;
import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.goldstandard.assurance.api.enumeration.PiiScanStatus;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PiiScanAsyncProcessor {

  private final PiiScanTransactionService transactionService;
  private final ProjectFinder projectFinder;
  private final ComprehendService comprehendService;
  private final Clock clock;

  @Async("piiScanExecutor")
  public void performPiiScanAsync(PiiScanRun scanRun, ProjectIdsDTO projectIdsDTO) {
    log.info("Starting async PII scan for run ID: {}", scanRun.getId());
    try {
      transactionService.updateScanStatus(scanRun, PiiScanStatus.RUNNING);
      var orderedProjectIds = projectIdsDTO.getProjectIds().stream().sorted().toList();
      for (var projectId : orderedProjectIds) {
        analyseProject(scanRun, projectId);
      }
      transactionService.updateScanStatus(scanRun, PiiScanStatus.COMPLETED);
    } catch (Exception e) {
      log.error("Error during PII scan for run ID: {}", scanRun.getId(), e);
      transactionService.updateScanStatusWithError(scanRun, PiiScanStatus.FAILED, e.getMessage());
    }
    log.info("Completed PII scan for run ID: {}", scanRun.getId());
  }

  private void analyseProject(PiiScanRun scanRun, Integer projectId) {
    try {
      log.info("Analysing project ID: {} in scan run ID: {}", projectId, scanRun.getId());
      transactionService.setProjectAnalysisAsRunning(scanRun.getId(), projectId);
      var project = projectFinder.forceGetProjectById(projectId);
      var fieldAnalysisList = getFieldTextAnalysisDTOS(project);
      var probablePiiResults = comprehendService.detectEntities(fieldAnalysisList);
      var findings = buildPiiFindings(probablePiiResults, scanRun, project);
      transactionService.setProjectAnalysisAsFinished(findings, scanRun, projectId);
      log.info(
          "Completed analysis for project ID: {} in scan run ID: {}", projectId, scanRun.getId());
    } catch (Exception e) {
      log.error("Error analysing project ID: {} in scan run ID: {}", projectId, scanRun.getId(), e);
      transactionService.setProjectAnalysisAsFailed(scanRun.getId(), projectId);
    }
  }

  private List<PiiFinding> buildPiiFindings(
      Collection<ProbablePii> probablePiiResults, PiiScanRun scanRun, Project project) {
    return probablePiiResults.stream()
        .map(
            probablePii ->
                PiiFinding.builder()
                    .piiScanRun(scanRun)
                    .entityType(probablePii.getEntity())
                    .projectId(project.getId())
                    .fieldName(probablePii.getField())
                    .piiType(PiiType.PROJECT)
                    .probability(probablePii.getProbability())
                    .createdAt(ZonedDateTime.now(clock))
                    .build())
        .toList();
  }

  private static List<FieldTextAnalysisDTO> getFieldTextAnalysisDTOS(Project project) {
    return Stream.of(
            FieldTextAnalysisDTO.builder().field("title").text(project.getName()).build(),
            FieldTextAnalysisDTO.builder()
                .field("description")
                .text(project.getDescription())
                .build(),
            FieldTextAnalysisDTO.builder().field("notes").text(project.getNotes()).build())
        .filter(dto -> dto.getText() != null && !dto.getText().trim().isEmpty())
        .toList();
  }
}
