package org.goldstandard.assurance.api.entity.view.stakeholder.project;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class ProjectBase {
  @Id private String gsid;
  private Integer projectId;
  private String projectName;
  private String projectStatus;
  private String description;
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
  private ZonedDateTime transitionCreditingPeriodStartDateOtherSchemes;
  private ZonedDateTime transitionCreditingPeriodEndDateOtherSchemes;
  private String transitionFromOtherSchemes;
  private String transitionPathway;
  private ZonedDateTime certifiedProjectDate;
  private ZonedDateTime lastDesignCertificationRenewalDate;
  private ZonedDateTime lastPerformanceReviewDate;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_location",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_location_id", referencedColumnName = "id"))
  private List<ProjectLocation> projectLocations;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_asset",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_asset_id", referencedColumnName = "id"))
  private List<ProjectAsset> projectAssets;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_sustainable_development_goal",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns =
          @JoinColumn(name = "sustainable_development_goal_id", referencedColumnName = "id"))
  private List<SustainableDevelopmentGoal> projectSustainabilityGoals;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_type",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_type_id", referencedColumnName = "id"))
  private List<ProjectType> projectTypes;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_methodology",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "methodology_id", referencedColumnName = "id"))
  private List<Methodology> projectMethodologies;
}
