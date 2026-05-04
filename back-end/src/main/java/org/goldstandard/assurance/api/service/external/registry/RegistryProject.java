package org.goldstandard.assurance.api.service.external.registry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;

@Builder
@Getter
@ToString
public class RegistryProject {

  @JsonIgnore private final Integer id;

  @JsonProperty("project_id")
  private final Integer projectId;

  @JsonProperty("poa_id")
  private final Integer poaId;

  @JsonProperty("sustaincert_id")
  private final Integer gsidNum;

  private final String name;
  private final String description;
  private final String status;

  @JsonProperty("gsf_standards_version")
  private final String standardsVersion;

  @JsonProperty("country_code")
  private final String countryCode;

  @JsonProperty("estimated_annual_credits")
  private final Integer estimatedAnnualCredits;

  @JsonProperty("crediting_period_start_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private final ZonedDateTime creditingPeriodStartDate;

  @JsonProperty("crediting_period_end_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private final ZonedDateTime creditingPeriodEndDate;

  private final String methodology;

  @JsonProperty("carbon_stream")
  private final String carbonStream;

  private final String type;
  private final String size;

  @JsonProperty("sustaincert_url")
  private final String url;

  @JsonProperty("sustainable_development_goals")
  private final Collection<String> sustainableDevelopmentGoals;

  @JsonProperty("project_developer")
  private final String projectDeveloper;

  @JsonProperty("programme_of_activities")
  private final String programOfActivities;

  @JsonProperty("crediting_periods")
  private final Collection<ExternalCreditingPeriod> creditingPeriods;

  @JsonProperty("design_certification_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private final ZonedDateTime designCertificationDate;
}
