package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateParentProjectDetailsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.repository.ProjectClassificationRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CandidateParentProjectFinder {

  private static final Comparator<ProjectMinimal> PROJECT_MINIMAL_DISPLAY_ORDER =
      Comparator.comparing(
              ProjectMinimal::getName, Comparator.nullsLast(String::compareToIgnoreCase))
          .thenComparing(ProjectMinimal::getGsid, Comparator.nullsLast(String::compareToIgnoreCase))
          .thenComparing(ProjectMinimal::getId, Comparator.nullsLast(Integer::compareTo));
  private static final Comparator<Project> PROJECT_DISPLAY_ORDER =
      Comparator.comparing(Project::getName, Comparator.nullsLast(String::compareToIgnoreCase))
          .thenComparing(Project::getGsid, Comparator.nullsLast(String::compareToIgnoreCase))
          .thenComparing(Project::getId, Comparator.nullsLast(Integer::compareTo));

  private final ProjectRepository projectRepository;
  private final ProjectClassificationRepository classificationRepository;
  private final PermissionsService permissionsService;
  private final ProjectAccessPolicy accessPolicy;
  private final CandidateParentEligibilityPolicy parentEligibilityPolicy;

  public Collection<ProjectMinimal> getCandidatePOAs(GetCandidatePOAsRequest request) {
    var classification = classificationRepository.forceFindById(request.getClassificationId());
    var effectiveAccountId = accessPolicy.resolveProjectAccountId(request.getAccountId());
    if (!canFindCandidatePOAs(classification)) {
      return Collections.emptyList();
    }
    var projects = getAvailablePOAs(effectiveAccountId, classification);
    if (classification.isLegacyVpaCpa()) {
      return projects.stream()
          .filter(project -> hasChildrenWithClassification(project, classification))
          .sorted(PROJECT_MINIMAL_DISPLAY_ORDER)
          .toList();
    }
    return projects.stream().sorted(PROJECT_MINIMAL_DISPLAY_ORDER).toList();
  }

  public Collection<Project> getCandidateRealCaseVPAs(GetCandidateRealCaseVPAsRequest request) {
    var effectiveAccountId = accessPolicy.resolveProjectAccountId(request.getAccountId());
    if (Objects.equals(
        request.getClassificationId(), ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID)) {
      return projectRepository
          .findAllRealCaseVPAsByMasterProjectId(request.getMasterProjectId())
          .stream()
          .sorted(PROJECT_DISPLAY_ORDER)
          .toList();
    }
    return projectRepository
        .findAllRealCaseVPAsByAccountIdAndMasterProjectId(
            effectiveAccountId, request.getMasterProjectId())
        .stream()
        .sorted(PROJECT_DISPLAY_ORDER)
        .toList();
  }

  public Project getCandidateParentDetails(GetCandidateParentProjectDetailsRequest request) {
    var classification = classificationRepository.forceFindById(request.getClassificationId());
    var effectiveAccountId = accessPolicy.resolveProjectAccountId(request.getAccountId());
    var candidateProject = forceGetProject(request.getCandidateProjectId());
    var masterProject = forceGetMasterProject(request);
    parentEligibilityPolicy.validateCandidateParentDetails(
        effectiveAccountId, classification, candidateProject, masterProject);
    return candidateProject;
  }

  private boolean canFindCandidatePOAs(ProjectClassification classification) {
    return classification.isRealCaseVpa()
        || classification.isRegularCaseVpa()
        || classification.isLegacyVpaCpa();
  }

  private Collection<ProjectMinimal> getAvailablePOAs(
      Integer accountId, ProjectClassification classification) {
    if (parentEligibilityPolicy.canUseCrossAccountParents(classification)
        || canAssignToAnyPOA(classification)) {
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

  private Project forceGetMasterProject(GetCandidateParentProjectDetailsRequest request) {
    if (Objects.isNull(request.getMasterProjectId())
        || Objects.equals(request.getMasterProjectId(), request.getCandidateProjectId())) {
      return null;
    }
    return forceGetProject(request.getMasterProjectId());
  }

  private Project forceGetProject(Integer projectId) {
    if (Objects.isNull(projectId)) {
      throw new BadRequestException("Candidate project id is required");
    }
    return projectRepository
        .findById(projectId)
        .orElseThrow(
            () -> new NotFoundException(String.format("Project with id %s not found", projectId)));
  }
}
