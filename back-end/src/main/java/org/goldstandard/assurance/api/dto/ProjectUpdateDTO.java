package org.goldstandard.assurance.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.validator.ConsistentCreditingPeriods;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
public class ProjectUpdateDTO {
  @NotNull
  @NotBlank(message = "Project Title is required.")
  @Length(max = 255, message = "Project Title must not exceed 255 characters.")
  private String name;

  @NotNull(message = "Account ID is required.")
  private Integer accountId;

  @Length(max = 9000, message = "Project Description must not exceed 9000 characters.")
  private String description;

  private Integer projectClassificationId;
  private Integer masterProjectId;
  private Integer parentProjectId;
  private Integer sizeId;

  @NotNull(message = "Project Status is required.")
  private Integer projectStatusId;

  private Integer currentStandardId;

  @NotNull(message = "Project Fee Schedule is required.")
  private Integer feeScheduleId;

  private Integer registrationTypeId;
  private Integer carbonStreamId;
  private List<Integer> projectAssetIds;
  private List<Integer> locationIds;
  private ZonedDateTime startDate;

  private Integer estimatedAnnualUnits;

  @NotNull
  @Size(min = 1, message = "At least one project type must be provided")
  private List<Integer> typeIds;

  private List<Integer> methodologyIds;

  private List<Integer> sustainableDevelopmentGoalIds;
  private String notes;

  private ZonedDateTime designCertificationDate;
  private ZonedDateTime certifiedProjectDate;
  private ZonedDateTime lastDesignCertificationRenewalDate;
  private ZonedDateTime lastPerformanceReviewDate;

  private ZonedDateTime listingDate;
  private Boolean listingDateManualSet;
  private TransitionProjectDetails transitionProject;

  @ConsistentCreditingPeriods private List<@Valid CreditingPeriodDTO> creditingPeriods;

  private TextAnalysisResultsDTO textAnalysisResults;
  private Boolean displayNotRequestedStatusRequests;
}
