package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BundleDTO;
import org.goldstandard.assurance.api.dto.CandidateParentMethodologyDTO;
import org.goldstandard.assurance.api.dto.CandidateParentProjectDetailsDTO;
import org.goldstandard.assurance.api.dto.MethodologyHistoryDTO;
import org.goldstandard.assurance.api.dto.MethodologyUpdateDTO;
import org.goldstandard.assurance.api.dto.PiiMetadataDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryDTO;
import org.goldstandard.assurance.api.dto.ProjectTreeNodeDTO;
import org.goldstandard.assurance.api.entity.*;
import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassificationSemantics;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.projection.RequestProject;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectRiskSummaryService;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectConverter {

  private final TransitionDetailConverter transitionDetailConverter;
  private final ProjectCreditingPeriodConverter creditingPeriodConverter;
  private final ProjectLocationConverter projectLocationConverter;
  private final PermissionsService permissionsService;
  private final ProjectRiskSummaryService projectRiskSummaryService;
  private final PiiMetadataService piiMetadataService;
  private final RegistryUiLinkFactory registryLinkFactory;

  public ProjectDTO toCandidateProjectDTO(Project project) {
    var builder =
        toOverviewProjectDtoBuilder(project)
            .creditingPeriods(creditingPeriodConverter.toDtos(project.getCreditingPeriods()));
    if (permissionsService.isAdminOrArm()) {
      builder.currentRiskSummary(
          toBasicProjectRiskSummaryDTO(
              projectRiskSummaryService.getCurrentRiskSummary(project.getId())));
    }
    return builder.build();
  }

  public CandidateParentProjectDetailsDTO toCandidateParentDetailsDto(Project project) {
    return CandidateParentProjectDetailsDTO.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .name(project.getName())
        .projectClassificationId(project.getProjectClassification().getId())
        .projectClassification(project.getProjectClassification())
        .projectStatus(project.getProjectStatus())
        .projectStartDate(project.getProjectStartDate())
        .carbonStream(project.getCarbonStream())
        .projectSize(project.getProjectSize())
        .projectTypes(project.getProjectTypes())
        .projectLocations(project.getProjectLocations())
        .projectAssets(project.getProjectAssets())
        .sustainableDevelopmentGoals(project.getSustainableDevelopmentGoals())
        .methodologies(
            project.getMethodologies().stream()
                .map(
                    methodology ->
                        new CandidateParentMethodologyDTO(
                            methodology.getId(),
                            methodology.getCode(),
                            methodology.getDescription()))
                .toList())
        .creditingPeriods(creditingPeriodConverter.toDtos(project.getCreditingPeriods()))
        .build();
  }

  public ProjectDTO toMinimalProjectDto(Project project) {
    return toMinimalProjectDtoBuilder(project).build();
  }

  public ProjectDTO toMinimalProjectDto(ProjectMinimal project) {
    return ProjectDTO.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .name(project.getName())
        .build();
  }

  public ProjectDTO toMinimalProjectDto(RequestProject project) {
    return ProjectDTO.builder()
        .id(project.getProjectId())
        .gsid(project.getProjectGsid())
        .name(project.getProjectName())
        .build();
  }

  public ProjectDTO toReferenceDto(Project project) {
    if (Objects.isNull(project)) {
      return null;
    }
    return ProjectDTO.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .name(project.getName())
        .projectClassificationId(project.getProjectClassification().getId())
        .piiMetadata(buildPiiMetadata(project))
        .build();
  }

  public ProjectDTO toOverviewProjectDto(Project project) {
    return toOverviewProjectDtoBuilder(project).build();
  }

  public ProjectDTO toDetailedProjectDto(Project project) {
    var builder =
        toOverviewProjectDtoBuilder(project)
            .projectClassification(
                ProjectClassificationSemantics.toDisplayClassification(
                    project.getProjectClassification()))
            .projectStartDate(project.getProjectStartDate())
            .registrationType(project.getRegistrationType())
            .hasProjectAssets(project.getHasProjectAssets())
            .carbonStream(project.getCarbonStream())
            .projectSize(project.getProjectSize())
            .currentRequestId(project.getCurrentRequestId())
            .bundle(Optional.ofNullable(project.getBundle()).map(BundleDTO::new).orElse(null))
            .designCertificationDate(project.getDesignCertificationDate())
            .designCertificationDateManualSet(project.getDesignCertificationDateManualSet())
            .feeSchedule(project.getFeeSchedule())
            .estimatedAnnualUnits(project.getEstimatedAnnualUnits())
            .listingDate(project.getListingDate())
            .listingDateManualSet(project.getListingDateManualSet())
            .projectLocations(project.getProjectLocations())
            .projectAssets(project.getProjectAssets())
            .transitionDetail(transitionDetailConverter.toDto(project.getTransitionProject()))
            .creditingPeriods(creditingPeriodConverter.toDtos(project.getCreditingPeriods()))
            .sustainableDevelopmentGoals(project.getSustainableDevelopmentGoals())
            .methodologies(project.getMethodologies())
            .lastActivity(project.getLastActivity())
            .notes(project.getNotes())
            .certifiedProjectDate(project.getCertifiedProjectDate())
            .lastDesignCertificationRenewalDate(project.getLastDesignCertificationRenewalDate())
            .lastPerformanceReviewDate(project.getLastPerformanceReviewDate())
            .piiMetadata(buildPiiMetadata(project))
            .displayNotRequestedStatusRequests(project.shouldDisplayNotRequestedStatusRequests())
            .registryUrl(registryLinkFactory.buildProjectLinkUrl(project).orElse(null));

    if (permissionsService.isAdminOrArm()) {
      builder.currentRiskSummary(
          toBasicProjectRiskSummaryDTO(
              projectRiskSummaryService.getCurrentRiskSummary(project.getId())));
    }

    return builder.build();
  }

  private ProjectDTO.ProjectDTOBuilder toMinimalProjectDtoBuilder(Project project) {
    return ProjectDTO.builder().id(project.getId()).gsid(project.getGsid()).name(project.getName());
  }

  private ProjectDTO.ProjectDTOBuilder toOverviewProjectDtoBuilder(Project project) {
    return toMinimalProjectDtoBuilder(project)
        .description(project.getDescription())
        .currentStandardVersion(project.getCurrentStandardVersion())
        .status(project.getStatus())
        .projectTypes(project.getProjectTypes())
        .projectStatus(project.getProjectStatus())
        .countries(projectLocationConverter.toDtos(project.getProjectLocations()))
        .accountId(project.getAccountId())
        .masterProject(toReferenceDto(project.getMasterProject()))
        .parentProject(toReferenceDto(project.getParentProject()))
        .projectClassificationId(project.getProjectClassification().getId())
        .migrated(project.getMigrated());
  }

  public ProjectTreeNodeDTO toDto(ProjectTree tree) {
    return toNodeDto(tree.getRoot());
  }

  public MethodologyHistoryDTO toMethodologyHistoryDTO(ProjectMethodologyUpdate history) {
    var builder =
        MethodologyHistoryDTO.builder()
            .id(history.getId())
            .projectId(history.getProjectId())
            .gsid(history.getGsid())
            .periodId(history.getPeriodId())
            .requestId(history.getRequestId())
            .requestName(history.getRequest().map(Request::getRequestName).orElse(null))
            .updatedAt(history.getCreatedTimestamp())
            .updatedByAccountId(null)
            .updatedByAccountName(null)
            .updates(toUpdateDtos(history.getMethodologyUpdates()))
            .verdict(history.getVerdict().name());
    var createdBy = history.getCreatedBy();
    if (permissionsService.isAdminOrArm() && createdBy.isPresent()) {
      builder
          .updatedByAccountId(createdBy.map(Account::getId).orElse(null))
          .updatedByAccountName(createdBy.map(Account::getName).orElse(null));
    }
    return builder.build();
  }

  private Collection<MethodologyUpdateDTO> toUpdateDtos(
      List<DbMethodologyUpdate> methodologyUpdates) {
    return methodologyUpdates.stream().map(this::toMethodologyUpdateDTO).toList();
  }

  private MethodologyUpdateDTO toMethodologyUpdateDTO(DbMethodologyUpdate update) {
    return MethodologyUpdateDTO.builder()
        .id(update.getMethodologyId())
        .updatedVersion(update.getNewMethodologyVersion())
        .originalVersion(update.getInitialMethodologyVersion())
        .operation(update.getOperation().name())
        .build();
  }

  private ProjectTreeNodeDTO toNodeDto(ProjectTreeNode node) {
    return ProjectTreeNodeDTO.builder()
        .gsid(node.getGsid())
        .id(node.getId())
        .name(node.getName())
        .accountId(node.getAccountId())
        .classificationId(node.getClassificationId())
        .bundle(Optional.ofNullable(node.getBundle()).map(BundleDTO::new).orElse(null))
        .projectTypes(node.getTypes())
        .projectStatus(node.getStatus())
        .countries(projectLocationConverter.toDtos(node.getLocations()))
        .children(toNodeDTOs(node.getChildren()))
        .build();
  }

  private Collection<ProjectTreeNodeDTO> toNodeDTOs(Collection<ProjectTreeNode> nodes) {
    return nodes.stream().map(this::toNodeDto).toList();
  }

  public ProjectRiskSummaryDTO toBasicProjectRiskSummaryDTO(ProjectRiskSummary entity) {
    if (entity == null) return null;
    return toProjectRiskSummaryDTO(entity).build();
  }

  public ProjectRiskSummaryDTO toDetailedProjectRiskSummaryDTO(ProjectRiskSummary entity) {
    if (entity == null) return null;
    return toProjectRiskSummaryDTO(entity)
        .user(Optional.ofNullable(entity.getUser()).map(User::getFullName).orElse(""))
        .build();
  }

  public ProjectRiskSummaryDTO.ProjectRiskSummaryDTOBuilder toProjectRiskSummaryDTO(
      ProjectRiskSummary entity) {
    return ProjectRiskSummaryDTO.builder()
        .projectId(entity.getProject().getId())
        .risk(entity.getRisk())
        .summary(entity.getSummary())
        .dateApplied(entity.getDateApplied());
  }

  private PiiMetadataDTO buildPiiMetadata(Project project) {
    var dtoBuilder = PiiMetadataDTO.builder();
    if (permissionsService.isAdminOrArm() || permissionsService.isProjectOwner(project)) {
      var metadataDetails = piiMetadataService.getNonActionedDetailsByProjectId(project.getId());
      dtoBuilder.piiPresent(!metadataDetails.isEmpty()).piiDetail(metadataDetails);
    }
    return dtoBuilder.build();
  }
}
