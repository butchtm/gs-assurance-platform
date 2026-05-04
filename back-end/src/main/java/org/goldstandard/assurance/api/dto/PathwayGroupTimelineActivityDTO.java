package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PathwayGroupTimelineActivityDTO {

  private final Integer index;
  private final String name;
  private final ZonedDateTime startDate;
  private final ZonedDateTime dueDate;
  private final ZonedDateTime endDate;
  private final boolean current;
  private final boolean externalResponsibility;
  private final boolean completed;
  private final boolean skipped;
  @JsonIgnore private final boolean harmonisationOfFeedback;
}
