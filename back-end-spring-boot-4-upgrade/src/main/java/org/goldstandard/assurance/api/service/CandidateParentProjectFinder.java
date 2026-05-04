package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.repository.ProjectClassificationRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CandidateParentProjectFinder {

  private final ProjectRepository projectRepository;
  private final ProjectClassificationRepository classificationRepository;
  private final PermissionsService permissionsService;

  public Collection<ProjectMinimal> getCandidatePOAs(GetCandidatePOAsRequest request) {
    var classification = classificationRepository.forceFindById(request.getClassificationId());
    if (!canFindCandidatePOAs(classification)) {
      return Collections.emptyList();
    }
    var projects = getAvailablePOAs(request.getAccountId(), classification);
    if (classification.isLegacyVpaCpa()) {
      return projects.stream()
          .filter(project -> hasChildrenWithClassification(project, classification))
          .toList();
    }
    return projects;
  }

  public Collection<Project> getCandidateRealCaseVPAs(GetCandidateRealCaseVPAsRequest request) {
    return projectRepository.findAllRealCaseVPAsByAccountIdAndMasterProjectId(
        request.getAccountId(), request.getMasterProjectId());
  }

  private boolean canFindCandidatePOAs(ProjectClassification classification) {
    return classification.isRealCaseVpa()
        || classification.isRegularCaseVpa()
        || classification.isLegacyVpaCpa();
  }

  private Collection<ProjectMinimal> getAvailablePOAs(
      Integer accountId, ProjectClassification classification) {
    if (canAssignToAnyPOA(classification)) {
      return projectRepository.findAllPOAs();
    }
    return projectRepository.findAllPOAsByAccountId(accountId);
  }

  private boolean canAssignToAnyPOA(ProjectClassification classification) {
    return permissionsService.isAdminOrArm()
        && (classification.isRealCaseVpa() || classification.isLegacyVpaCpa());
  }

  private boolean hasChildrenWithClassification(
      ProjectMinimal parent, ProjectClassification classification) {
    return projectRepository.existsByMasterProjectIdAndProjectClassificationId(
        parent.getId(), classification.getId());
  }
}
