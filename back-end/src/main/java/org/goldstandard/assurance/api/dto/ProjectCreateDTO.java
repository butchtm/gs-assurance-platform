package org.goldstandard.assurance.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.dto.validator.ConsistentCreditingPeriods;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ProjectCreateDTO {

  @NotNull
  @NotBlank(message = "Project Title is required.")
  @Length(max = 255, message = "Project Title must not exceed 255 characters.")
  private String name;

  @Length(max = 9000, message = "Project Description must not exceed 9000 characters.")
  private String description;

  @NotNull(message = "Project Classification is required.")
  private Integer projectClassificationId;

  private Integer masterProjectId;
  private Integer parentProjectId;
  private Integer sizeId;
  private Integer currentStandardId;

  @NotNull(message = "Project Fee Schedule is required.")
  private Integer feeScheduleId;

  private Integer registrationTypeId;
  private Integer carbonStreamId;
  private List<Integer> projectAssetIds;
  private List<Integer> locationIds;
  private ZonedDateTime startDate;

  @Min(value = 1, message = "Estimated Annual Emission Reduction must be at least 1.")
  @Max(
      value = 99999999,
      message = "Estimated Annual Emission Reduction must not exceed 99,999,999.")
  private Integer estimatedAnnualUnits;

  @NotNull private Integer accountId;
  @NotNull private Integer projectStatusId;

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
  private TransitionProjectDetails transitionProject;
  private TextAnalysisResultsDTO textAnalysisResults;

  @ConsistentCreditingPeriods private List<@Valid CreditingPeriodDTO> creditingPeriods;
}
