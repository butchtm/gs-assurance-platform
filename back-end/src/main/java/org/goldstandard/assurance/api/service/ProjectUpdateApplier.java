package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.dto.TransitionProjectDetails;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.TransitionProject;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.RegistrationType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.entity.lookup.TransitionApprovalProcedure;
import org.goldstandard.assurance.api.entity.lookup.TransitionFromOtherScheme;
import org.goldstandard.assurance.api.entity.lookup.TransitionPathway;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Component
public class ProjectUpdateApplier {

  private final AuthenticatedUserSupplier authenticatedUserSupplier;

  public Project applyUpdates(Project project, ProjectUpdateDTO dto) {
    var updatedProject =
        project.toBuilder()
            .name(dto.getName())
            .accountId(dto.getAccountId())
            .description(dto.getDescription())
            .projectClassification(
                convertIfPresent(dto.getProjectClassificationId(), ProjectClassification::new))
            .masterProject(convertIfPresent(dto.getMasterProjectId(), Project::new))
            .parentProject(convertIfPresent(dto.getParentProjectId(), Project::new))
            .projectSize(convertIfPresent(dto.getSizeId(), ProjectSize::new))
            .currentStandardVersion(
                convertIfPresent(dto.getCurrentStandardId(), CurrentStandardVersion::new))
            .feeSchedule(convertIfPresent(dto.getFeeScheduleId(), FeeSchedule::new))
            .registrationType(convertIfPresent(dto.getRegistrationTypeId(), RegistrationType::new))
            .carbonStream(convertIfPresent(dto.getCarbonStreamId(), CarbonStream::new))
            .projectAssets(toRefDataItems(dto.getProjectAssetIds(), ProjectAsset::new))
            .projectLocations(toRefDataItems(dto.getLocationIds(), ProjectLocation::new))
            .projectStartDate(dto.getStartDate())
            .estimatedAnnualUnits(dto.getEstimatedAnnualUnits())
            .projectTypes(toRefDataItems(dto.getTypeIds(), ProjectType::new))
            .methodologies(toRefDataItems(dto.getMethodologyIds(), Methodology::new))
            .sustainableDevelopmentGoals(
                toRefDataItems(
                    dto.getSustainableDevelopmentGoalIds(), SustainableDevelopmentGoal::new))
            .notes(dto.getNotes())
            .designCertificationDate(dto.getDesignCertificationDate())
            .certifiedProjectDate(dto.getCertifiedProjectDate())
            .lastDesignCertificationRenewalDate(dto.getLastDesignCertificationRenewalDate())
            .lastPerformanceReviewDate(dto.getLastPerformanceReviewDate())
            .updatedByUserId(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId())
            .listingDate(dto.getListingDate())
            .listingDateManualSet(dto.getListingDateManualSet())
            .displayNotRequestedStatusRequests(dto.getDisplayNotRequestedStatusRequests())
            .build();
    return updatedProject.withTransitionProject(
        updateTransitionProject(updatedProject, dto.getTransitionProject()));
  }

  public Project applyProjectDeveloperUpdates(Project project, ProjectUpdateDTO dto) {
    return project.toBuilder()
        .name(dto.getName())
        .projectClassification(
            convertIfPresent(dto.getProjectClassificationId(), ProjectClassification::new))
        .updatedByUserId(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId())
        .build();
  }

  private static TransitionProject updateTransitionProject(
      Project project, TransitionProjectDetails newDetail) {
    if (newDetail == null) {
      return null;
    }

    var builder =
        Optional.ofNullable(project.getTransitionProject())
            .map(TransitionProject::toBuilder)
            .orElse(TransitionProject.builder());

    return builder
        .project(project)
        .projectLinkInOtherScheme(newDetail.getProjectLinkInOtherScheme())
        .creditingPeriodStartDateOtherSchemes(newDetail.getCreditingPeriodStartDateOtherSchemes())
        .creditingPeriodEndDateOtherSchemes(newDetail.getCreditingPeriodEndDateOtherSchemes())
        .transitionApprovalProcedure(
            convertIfPresent(
                newDetail.getTransitionApprovalProcedureId(), TransitionApprovalProcedure::new))
        .transitionFromOtherScheme(
            convertIfPresent(
                newDetail.getTransitionFromOtherSchemeId(), TransitionFromOtherScheme::new))
        .transitionPathway(
            convertIfPresent(newDetail.getTransitionPathwayId(), TransitionPathway::new))
        .build();
  }

  private static <T> T convertIfPresent(Integer id, IntFunction<T> function) {
    if (Objects.isNull(id)) {
      return null;
    }
    return function.apply(id);
  }

  private static <T extends ReferenceDataType> List<T> toRefDataItems(
      Collection<Integer> ids, Function<Integer, T> function) {
    if (CollectionUtils.isEmpty(ids)) {
      return new ArrayList<>();
    }
    return new ArrayList<>(ids.stream().distinct().map(function).toList());
  }
}
