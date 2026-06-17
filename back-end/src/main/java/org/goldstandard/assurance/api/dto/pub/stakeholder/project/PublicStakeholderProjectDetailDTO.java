package org.goldstandard.assurance.api.dto.pub.stakeholder.project;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.request.PublicStakeholderRequestDetailDTO;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassificationSemantics;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBase;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicStakeholderProjectDetailDTO {
  private String gsid;
  private String name;
  private String projectStatus;
  private String projectDescription;
  private String accountName;
  private String currentStandardVersion;
  private Integer masterProject;
  private Integer parentProject;
  private String masterProjectGsid;
  private String projectClassification;
  private ZonedDateTime projectStartDate;
  private String registrationType;
  private Boolean hasProjectAssets;
  private String carbonStream;
  private String projectSize;
  private Integer bundleId;
  private ZonedDateTime designCertificationDate;
  private Integer estimatedAnnualUnits;
  private ZonedDateTime listingDate;
  private String transitionProjectLinkInOtherScheme;
  private ZonedDateTime transitionCreditingPeriodStartDate;
  private ZonedDateTime transitionCreditingPeriodEndDate;
  private String transitionFromOtherScheme;
  private String transitionPathway;
  private ZonedDateTime certifiedProjectDate;
  private ZonedDateTime lastDesignCertificationRenewalDate;
  private ZonedDateTime lastPerformanceReviewDate;
  private List<String> projectLocations;
  private List<String> projectAssets;
  private List<String> projectSustainabilityGoals;
  private List<SustainableDevelopmentGoal> projectSustainabilityGoalsTypes;
  private List<String> projectTypes;
  private List<String> projectMethodologies;
  private List<CreditingPeriodDTO> projectCreditingPeriods;
  private List<PublicStakeholderRequestDetailDTO> requests;

  public PublicStakeholderProjectDetailDTO(ProjectBase project) {
    this.gsid = project.getGsid();
    this.name = project.getProjectName();
    this.projectStatus = project.getProjectStatus();
    this.projectDescription = project.getDescription();
    this.accountName = project.getAccountName();
    this.currentStandardVersion = project.getCurrentStandardVersion();
    this.masterProject = project.getMasterProject();
    this.parentProject = project.getParentProject();
    this.masterProjectGsid = project.getMasterProjectGsid();
    this.projectClassification =
        ProjectClassificationSemantics.toDisplayDescription(project.getProjectClassification());
    this.projectStartDate = project.getProjectStartDate();
    this.registrationType = project.getRegistrationType();
    this.hasProjectAssets = project.getHasProjectAssets();
    this.carbonStream = project.getCarbonStream();
    this.projectSize = project.getProjectSize();
    this.bundleId = project.getBundleId();
    this.designCertificationDate = project.getDesignCertificationDate();
    this.estimatedAnnualUnits = project.getEstimatedAnnualUnits();
    this.listingDate = project.getListingDate();
    this.transitionProjectLinkInOtherScheme = project.getTransitionProjectLinkInOtherScheme();
    this.transitionCreditingPeriodStartDate =
        project.getTransitionCreditingPeriodStartDateOtherSchemes();
    this.transitionCreditingPeriodEndDate =
        project.getTransitionCreditingPeriodEndDateOtherSchemes();
    this.transitionFromOtherScheme = project.getTransitionFromOtherSchemes();
    this.transitionPathway = project.getTransitionPathway();
    this.certifiedProjectDate = project.getCertifiedProjectDate();
    this.lastDesignCertificationRenewalDate = project.getLastDesignCertificationRenewalDate();
    this.lastPerformanceReviewDate = project.getLastPerformanceReviewDate();
    this.projectAssets =
        emptyIfNull(project.getProjectAssets()).stream().map(ProjectAsset::getDescription).toList();
    this.projectLocations =
        emptyIfNull(project.getProjectLocations()).stream()
            .map(ProjectLocation::getDescription)
            .toList();
    this.projectMethodologies =
        emptyIfNull(project.getProjectMethodologies()).stream()
            .map(Methodology::getDescription)
            .toList();
    this.projectTypes =
        emptyIfNull(project.getProjectTypes()).stream().map(ProjectType::getDescription).toList();
    this.projectSustainabilityGoals =
        emptyIfNull(project.getProjectSustainabilityGoals()).stream()
            .map(SustainableDevelopmentGoal::getDescription)
            .toList();
    this.projectSustainabilityGoalsTypes = project.getProjectSustainabilityGoals();
  }
}
