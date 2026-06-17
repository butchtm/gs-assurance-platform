package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateParentProjectDetailsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.dto.ProjectCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectStatusUpdateDTO;
import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.entity.ProjectTree;
import org.goldstandard.assurance.api.entity.TransitionProject;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.RegistrationType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.entity.lookup.TransitionApprovalProcedure;
import org.goldstandard.assurance.api.entity.lookup.TransitionFromOtherScheme;
import org.goldstandard.assurance.api.entity.lookup.TransitionPathway;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.service.external.ExternalUpdateExecutor;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class ProjectService {

  private final ProjectFinder finder;
  private final AuthenticatedUserSupplier authenticatedUserSupplier;
  private final ProjectAccessPolicy accessPolicy;
  private final ProjectTransaction transaction;
  private final ProjectTreeFactory projectTreeFactory;
  private final ProjectStatusUpdatePolicy projectStatusUpdatePolicy;
  private final ExternalUpdateExecutor externalUpdateExecutor;
  private final ProjectCurrentRequestUpdater currentRequestUpdater;
  private final CandidateParentProjectFinder candidateParentProjectFinder;
  private final MethodologyHistoryService methodologyHistoryService;
  private final CreditingPeriodFactory creditingPeriodFactory;
  private final ProjectUpdateApplier updateApplier;
  private final CandidateParentEligibilityPolicy parentEligibilityPolicy;
  private final PermissionsService permissionsService;

  public Project getProjectById(Integer projectId) {
    var project = finder.forceGetProjectById(projectId);
    accessPolicy.validateUserCanViewProject(project);
    currentRequestUpdater.updateCurrentRequestIfRequired(project);
    return project;
  }

  public Project getProjectByGsId(String gsid) {
    var project = finder.forceGetProjectByGsId(gsid);
    accessPolicy.validateUserCanViewProject(project);
    currentRequestUpdater.updateCurrentRequestIfRequired(project);
    return project;
  }

  public Page<Project> getProjects(Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    return finder.getProjects(pageable, searchCriteria);
  }

  public Page<ProjectTree> getProjectTrees(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    return finder.getProjectTrees(pageable, searchCriteria);
  }

  public List<Project> getByIds(Set<Integer> ids) {
    return finder.getByIds(ids);
  }

  public Project createProject(ProjectCreateDTO createRequest) {
    var effectiveAccountId = accessPolicy.resolveProjectAccountId(createRequest.getAccountId());
    validateCreateParentEligibility(createRequest, effectiveAccountId);
    var project = new Project();
    initialiseProject(project, createRequest, effectiveAccountId);
    var creditingPeriods =
        creditingPeriodFactory.toCreditingPeriods(project, createRequest.getCreditingPeriods());
    var savedProject =
        transaction.performCreate(
            project, creditingPeriods, createRequest.getTextAnalysisResults());
    externalUpdateExecutor.executeAsync(savedProject.getId());
    return savedProject;
  }

  public Project updateProject(Integer projectId, ProjectUpdateDTO projectUpdate) {
    var originalProject = findProjectIfAccessible(projectId);
    validateUpdateKeepsAccountAndHierarchy(originalProject, projectUpdate);
    var updatedProject = updateApplier.applyUpdates(originalProject, projectUpdate);
    var creditingPeriods =
        creditingPeriodFactory.toCreditingPeriods(
            updatedProject, projectUpdate.getCreditingPeriods());
    var savedProjects =
        transaction.performUpdate(
            originalProject,
            updatedProject,
            creditingPeriods,
            projectUpdate.getTextAnalysisResults());
    externalUpdateExecutor.executeAsync(savedProjects);
    return IterableUtils.get(savedProjects, 0);
  }

  public Collection<Project> updateProjectStatus(
      Integer projectId, ProjectStatusUpdateDTO updateDTO) {
    var updatedProjects =
        projectStatusUpdatePolicy.calculateProjectStatusUpdates(
            projectId, updateDTO.getRequestId(), updateDTO.getProjectStatusId());
    var savedProjects = transaction.performStatusUpdate(updatedProjects);
    externalUpdateExecutor.executeAsync(savedProjects);
    return savedProjects;
  }

  public ProjectTree getProjectTree(Integer projectId) {
    var project = finder.forceGetProjectById(projectId);
    accessPolicy.validateUserCanViewProject(project);
    if (permissionsService.canAccessAllProjects()) {
      return projectTreeFactory.buildProjectTree(project.getId());
    }
    return projectTreeFactory.buildAuthorizedProjectTree(
        project.getId(), getAuthorizedProjectTreeIds(projectId));
  }

  public Collection<ProjectMinimal> getCandidatePOAs(GetCandidatePOAsRequest request) {
    return candidateParentProjectFinder.getCandidatePOAs(request);
  }

  public Page<ProjectMinimal> getProjectsMinimal(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    return finder.getProjectsMinimal(pageable, searchCriteria);
  }

  public Collection<Project> getCandidateRealCaseVPAs(GetCandidateRealCaseVPAsRequest request) {
    return candidateParentProjectFinder.getCandidateRealCaseVPAs(request);
  }

  public Project getCandidateParentDetails(GetCandidateParentProjectDetailsRequest request) {
    return candidateParentProjectFinder.getCandidateParentDetails(request);
  }

  public Page<ProjectMethodologyUpdate> getMethodologyHistory(
      Integer projectId, Integer periodId, Pageable pageable) {
    return methodologyHistoryService.getHistory(projectId, periodId, pageable);
  }

  private Project findProjectIfAccessible(Integer projectId) {
    var project = finder.forceGetProjectById(projectId);
    accessPolicy.validateCanUpdateProject(project);
    return project;
  }

  private Collection<Integer> getAuthorizedProjectTreeIds(Integer requestedProjectId) {
    return permissionsService
        .getAccountIdForUser()
        .map(finder::getProjectIdsByAccountId)
        .orElse(Set.of(requestedProjectId));
  }

  private void validateCreateParentEligibility(
      ProjectCreateDTO createRequest, Integer effectiveAccountId) {
    var targetClassification =
        new ProjectClassification(createRequest.getProjectClassificationId());
    if (Objects.isNull(targetClassification.getId())
        || (!targetClassification.isRealCaseVpa() && !targetClassification.isRegularCaseVpa())) {
      return;
    }

    var masterProject = findProjectIfPresent(createRequest.getMasterProjectId());
    var parentProject = findProjectIfPresent(createRequest.getParentProjectId());
    parentEligibilityPolicy.validateParentEligibility(
        effectiveAccountId, targetClassification, masterProject, parentProject);
  }

  private Project findProjectIfPresent(Integer projectId) {
    if (Objects.isNull(projectId)) {
      return null;
    }
    return finder.forceGetProjectById(projectId);
  }

  private void validateUpdateKeepsAccountAndHierarchy(
      Project originalProject, ProjectUpdateDTO projectUpdate) {
    assertUnchanged("accountId", originalProject.getAccountId(), projectUpdate.getAccountId());
    assertUnchanged(
        "projectClassificationId",
        getReferenceId(originalProject.getProjectClassification()),
        projectUpdate.getProjectClassificationId());
    assertUnchanged(
        "masterProjectId",
        getReferenceId(originalProject.getMasterProject()),
        projectUpdate.getMasterProjectId());
    assertUnchanged(
        "parentProjectId",
        getReferenceId(originalProject.getParentProject()),
        projectUpdate.getParentProjectId());
  }

  private void assertUnchanged(String fieldName, Integer existingId, Integer requestedId) {
    if (Objects.equals(existingId, requestedId)) {
      return;
    }
    throw new BadRequestException("Project %s cannot be changed by project update", fieldName);
  }

  private static Integer getReferenceId(ReferenceDataType reference) {
    return Objects.isNull(reference) ? null : reference.getId();
  }

  private static Integer getReferenceId(Project project) {
    return Objects.isNull(project) ? null : project.getId();
  }

  private void initialiseProject(
      Project project, ProjectCreateDTO createRequest, Integer effectiveAccountId) {
    project.setName(createRequest.getName());
    project.setDescription(createRequest.getDescription());
    project.setProjectStartDate(createRequest.getStartDate());
    project.setEstimatedAnnualUnits(createRequest.getEstimatedAnnualUnits());

    if (createRequest.getCarbonStreamId() != null) {
      project.setCarbonStream(new CarbonStream(createRequest.getCarbonStreamId()));
    }
    if (createRequest.getCurrentStandardId() != null) {
      project.setCurrentStandardVersion(
          new CurrentStandardVersion(createRequest.getCurrentStandardId()));
    }
    if (createRequest.getFeeScheduleId() != null) {
      project.setFeeSchedule(new FeeSchedule(createRequest.getFeeScheduleId()));
    }
    if (createRequest.getProjectClassificationId() != null) {
      project.setProjectClassification(
          new ProjectClassification(createRequest.getProjectClassificationId()));
    }
    if (createRequest.getSizeId() != null) {
      project.setProjectSize(new ProjectSize(createRequest.getSizeId()));
    }
    if (createRequest.getRegistrationTypeId() != null) {
      project.setRegistrationType(new RegistrationType(createRequest.getRegistrationTypeId()));
    }
    project.setProjectStatus(new ProjectStatus(createRequest.getProjectStatusId()));

    project.setProjectLocations(
        toRefDataItems(createRequest.getLocationIds(), ProjectLocation::new));
    project.setProjectAssets(toRefDataItems(createRequest.getProjectAssetIds(), ProjectAsset::new));
    project.setHasProjectAssets(!project.getProjectAssets().isEmpty());
    project.setAccountId(effectiveAccountId);

    // We don't really know what status means but all existing projects have status true so hard
    // code for now
    project.setStatus(true);

    project.setMasterProject(
        createRequest.getMasterProjectId() != null
            ? new Project(createRequest.getMasterProjectId())
            : null);
    project.setParentProject(
        createRequest.getParentProjectId() != null
            ? new Project(createRequest.getParentProjectId())
            : null);

    project.setProjectTypes(toRefDataItems(createRequest.getTypeIds(), ProjectType::new));
    project.setMethodologies(toRefDataItems(createRequest.getMethodologyIds(), Methodology::new));
    project.setSustainableDevelopmentGoals(
        toRefDataItems(
            createRequest.getSustainableDevelopmentGoalIds(), SustainableDevelopmentGoal::new));

    project.setNotes(createRequest.getNotes());
    project.setCertifiedProjectDate(createRequest.getCertifiedProjectDate());
    project.setLastDesignCertificationRenewalDate(
        createRequest.getLastDesignCertificationRenewalDate());
    project.setLastPerformanceReviewDate(createRequest.getLastPerformanceReviewDate());

    project.setUpdatedByUserId(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId());
    if (createRequest.getTransitionProject() != null) {
      project.setTransitionProject(createTransitionProject(createRequest));
    }
    project.setDisplayNotRequestedStatusRequests(false);
  }

  private static TransitionProject createTransitionProject(ProjectCreateDTO projectDetails) {
    var details = projectDetails.getTransitionProject();
    return TransitionProject.builder()
        .projectLinkInOtherScheme(details.getProjectLinkInOtherScheme())
        .creditingPeriodStartDateOtherSchemes(details.getCreditingPeriodStartDateOtherSchemes())
        .creditingPeriodEndDateOtherSchemes(details.getCreditingPeriodEndDateOtherSchemes())
        .transitionPathway(new TransitionPathway(details.getTransitionPathwayId()))
        .transitionApprovalProcedure(
            new TransitionApprovalProcedure(details.getTransitionApprovalProcedureId()))
        .transitionFromOtherScheme(
            new TransitionFromOtherScheme(details.getTransitionFromOtherSchemeId()))
        .build();
  }

  private static <T extends ReferenceDataType> List<T> toRefDataItems(
      Collection<Integer> ids, Function<Integer, T> function) {
    if (CollectionUtils.isEmpty(ids)) {
      return new ArrayList<>();
    }
    return new ArrayList<>(ids.stream().distinct().map(function).toList());
  }
}
