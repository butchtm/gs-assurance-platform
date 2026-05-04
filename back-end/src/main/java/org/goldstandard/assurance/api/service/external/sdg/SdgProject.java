package org.goldstandard.assurance.api.service.external.sdg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;

@Builder
@Getter
@ToString
public class SdgProject {
  @JsonIgnore private final Integer id;
  @JsonIgnore private final String gsid;

  @JsonProperty("poa_project_id")
  private final Integer poaProjectId;

  private final String name;
  private final String description;
  private final String status;

  @JsonProperty("country_codes")
  private final Collection<String> countryCodes;

  @JsonProperty("annual_estimated_credits")
  private final Integer annualEstimatedCredits;

  @JsonProperty("carbon_stream")
  private final String carbonStream;

  private final Collection<String> types;

  @JsonProperty("project_developer")
  private final String projectDeveloper;

  @JsonProperty("programme_of_activities")
  private final String programOfActivities;

  @JsonProperty("crediting_periods")
  private final Collection<ExternalCreditingPeriod> creditingPeriods;
}
