package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder(toBuilder = true)
public class PathwayGroupTimelineActivityDTO {

  private final Integer index;
  private final String name;
  private final ZonedDateTime startDate;
  private final ZonedDateTime dueDate;
  private final ZonedDateTime endDate;
  private final Boolean current;
  private final Boolean externalResponsibility;
  private final Boolean completed;
  private final Boolean skipped;
  @JsonIgnore private final Boolean harmonisationOfFeedback;

  public boolean isCurrent() {
    return isTrue(current);
  }

  public boolean isExternalResponsibility() {
    return isTrue(externalResponsibility);
  }

  public boolean isCompleted() {
    return isTrue(completed);
  }

  public boolean isSkipped() {
    return isTrue(skipped);
  }

  public boolean isHarmonisationOfFeedback() {
    return isTrue(harmonisationOfFeedback);
  }

  private static boolean isTrue(Boolean value) {
    return Boolean.TRUE.equals(value);
  }
}
