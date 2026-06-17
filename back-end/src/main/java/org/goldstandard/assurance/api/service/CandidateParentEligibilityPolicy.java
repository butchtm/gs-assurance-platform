package org.goldstandard.assurance.api.service;

import java.util.Objects;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CandidateParentEligibilityPolicy {

  public void validateParentEligibility(
      Integer ownerAccountId,
      ProjectClassification targetClassification,
      Project masterProject,
      Project parentProject) {
    if (Objects.isNull(targetClassification) || Objects.isNull(targetClassification.getId())) {
      throw new BadRequestException("Project classification is required");
    }
    var canUseCrossAccountParents = canUseCrossAccountParents(targetClassification);
    if (targetClassification.isRealCaseVpa()) {
      validateRealCaseVpaParents(masterProject, parentProject);
      if (!canUseCrossAccountParents) {
        validateAccountScopedParents(ownerAccountId, masterProject, parentProject);
      }
      return;
    }
    if (targetClassification.isRegularCaseVpa()) {
      validateRegularVpaParents(masterProject, parentProject);
      if (!canUseCrossAccountParents) {
        validateAccountScopedParents(ownerAccountId, masterProject, parentProject);
      }
    }
  }

  public void validateCandidateParentDetails(
      Integer ownerAccountId,
      ProjectClassification targetClassification,
      Project candidateProject,
      Project masterProject) {
    if (Objects.isNull(targetClassification) || Objects.isNull(targetClassification.getId())) {
      throw new BadRequestException("Project classification is required");
    }
    var canUseCrossAccountParents = canUseCrossAccountParents(targetClassification);
    if (targetClassification.isRealCaseVpa() || targetClassification.isLegacyVpaCpa()) {
      validatePoaCandidate(candidateProject);
      if (!canUseCrossAccountParents) {
        validateProjectBelongsToAccount(ownerAccountId, candidateProject);
      }
      return;
    }
    if (targetClassification.isRegularCaseVpa()) {
      validateRegularVpaCandidate(candidateProject, masterProject);
      if (!canUseCrossAccountParents) {
        validateProjectBelongsToAccount(ownerAccountId, candidateProject);
        if (!isPoa(candidateProject)) {
          validateProjectBelongsToAccount(ownerAccountId, masterProject);
        }
      }
      return;
    }
    throw new BadRequestException("Project classification cannot use parent candidates");
  }

  public boolean canUseCrossAccountParents(ProjectClassification classification) {
    return hasClassificationId(classification, ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID)
        || hasClassificationId(classification, ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
  }

  private void validateRealCaseVpaParents(Project masterProject, Project parentProject) {
    if (!isPoa(masterProject) || !haveSameIds(masterProject, parentProject)) {
      throw new BadRequestException("Real Case VPA requires the selected parent to be a PoA");
    }
  }

  private void validateRegularVpaParents(Project masterProject, Project parentProject) {
    if (!isPoa(masterProject)
        || !isRealCaseVpa(parentProject)
        || !hasMasterProject(parentProject, masterProject)) {
      throw new BadRequestException(
          "Regular VPA requires a Real Case VPA parent under the selected PoA");
    }
  }

  private void validatePoaCandidate(Project candidateProject) {
    if (!isPoa(candidateProject)) {
      throw new BadRequestException("Candidate parent project must be a PoA");
    }
  }

  private void validateRegularVpaCandidate(Project candidateProject, Project masterProject) {
    if (isPoa(candidateProject)) {
      return;
    }
    if (!isPoa(masterProject)
        || !isRealCaseVpa(candidateProject)
        || !hasMasterProject(candidateProject, masterProject)) {
      throw new BadRequestException(
          "Regular VPA requires a Real Case VPA parent under the selected PoA");
    }
  }

  private void validateAccountScopedParents(
      Integer ownerAccountId, Project masterProject, Project parentProject) {
    validateProjectBelongsToAccount(ownerAccountId, masterProject);
    if (haveSameIds(masterProject, parentProject)) {
      return;
    }
    validateProjectBelongsToAccount(ownerAccountId, parentProject);
  }

  private void validateProjectBelongsToAccount(Integer ownerAccountId, Project project) {
    if (Objects.isNull(ownerAccountId)
        || Objects.isNull(project)
        || !Objects.equals(ownerAccountId, project.getAccountId())) {
      throw new BadRequestException("Selected parent project is not eligible for this account");
    }
  }

  private boolean isPoa(Project project) {
    return Objects.nonNull(project)
        && Objects.nonNull(project.getProjectClassification())
        && project.getProjectClassification().isPoa();
  }

  private boolean isRealCaseVpa(Project project) {
    return Objects.nonNull(project)
        && Objects.nonNull(project.getProjectClassification())
        && project.getProjectClassification().isRealCaseVpa();
  }

  private boolean hasMasterProject(Project project, Project masterProject) {
    return Objects.nonNull(project)
        && Objects.nonNull(project.getMasterProject())
        && haveSameIds(project.getMasterProject(), masterProject);
  }

  private boolean haveSameIds(Project first, Project second) {
    return Objects.nonNull(first)
        && Objects.nonNull(first.getId())
        && Objects.nonNull(second)
        && Objects.equals(first.getId(), second.getId());
  }

  private boolean hasClassificationId(ProjectClassification classification, Integer id) {
    return Objects.nonNull(classification) && Objects.equals(classification.getId(), id);
  }
}
