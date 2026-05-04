package org.goldstandard.assurance.api.dto;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AssignmentSummaryFiltersDTO {

  @Nullable private final Integer assignmentTypeId;
  @Nullable private final Boolean qaqcAssignedToCallingAccount;
  private final ZonedDateTime lastSubmittedAfter;
  private final ZonedDateTime lastSubmittedBefore;
  private final ZonedDateTime requestDueBefore;
  private final List<Integer> projectTypeIds;
  private final List<Integer> requestTypeIds;

  public Optional<ZonedDateTime> getLastSubmittedAfter() {
    return Optional.ofNullable(lastSubmittedAfter);
  }

  public Optional<ZonedDateTime> getLastSubmittedBefore() {
    return Optional.ofNullable(lastSubmittedBefore);
  }

  public Optional<ZonedDateTime> getRequestDueBefore() {
    return Optional.ofNullable(requestDueBefore);
  }

  public boolean isQaqcAssignedToCallingAccount() {
    return Boolean.TRUE.equals(qaqcAssignedToCallingAccount);
  }
}
