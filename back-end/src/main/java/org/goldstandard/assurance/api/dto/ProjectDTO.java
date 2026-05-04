package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import org.goldstandard.assurance.api.entity.lookup.RegistrationType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class ProjectDTO {
  private final Integer id;
  private final String gsid;
  private final String name;
  private final String description;
  private final Integer accountId;
  private final CurrentStandardVersion currentStandardVersion;
  private final Boolean status;
  private final List<ProjectType> projectTypes;
  private final ProjectStatus projectStatus;
  private final List<CountryDTO> countries;
  private final ProjectDTO masterProject;
  private final ProjectDTO parentProject;
  private final Integer projectClassificationId;
  private final Boolean migrated;
  private final ProjectClassification projectClassification;
  private final ZonedDateTime projectStartDate;
  private final RegistrationType registrationType;
  private final Boolean hasProjectAssets;
  private final CarbonStream carbonStream;
  private final ProjectSize projectSize;
  private final Integer currentRequestId;
  private final BundleDTO bundle;
  private final ZonedDateTime designCertificationDate;
  private final Boolean designCertificationDateManualSet;
  private final FeeSchedule feeSchedule;
  private final Integer estimatedAnnualUnits;
  private final ZonedDateTime listingDate;
  private final Boolean listingDateManualSet;
  private final List<ProjectLocation> projectLocations;
  private final List<ProjectAsset> projectAssets;
  private final TransitionDetail transitionDetail;
  private final List<CreditingPeriodDTO> creditingPeriods;
  private final List<SustainableDevelopmentGoal> sustainableDevelopmentGoals;
  private final List<Methodology> methodologies;
  private final ProjectRiskSummaryDTO currentRiskSummary;
  private final ZonedDateTime lastActivity;
  private final String notes;
  private final ZonedDateTime certifiedProjectDate;
  private final ZonedDateTime lastDesignCertificationRenewalDate;
  private final ZonedDateTime lastPerformanceReviewDate;
  private final PiiMetadataDTO piiMetadata;
  private final Boolean displayNotRequestedStatusRequests;
  private final String registryUrl;
}
