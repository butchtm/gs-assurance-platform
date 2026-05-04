package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CommonDashboardRequestFiltersDTO {
  private final Collection<Integer> requestStatusIds;
  private final Collection<Integer> invoiceStatusIds;
  private final Collection<Integer> requestTypeIds;
  private final Collection<Integer> projectClassificationIds;
  private final Collection<Integer> roundOfReviewCounts;
  private final Collection<Integer> projectTypeIds;
  private final ZonedDateTime firstRoundDateStart;
  private final ZonedDateTime firstRoundDateEnd;
  private final String projectSearch;

  public Collection<Integer> getRequestStatusIds() {
    return toEmptyIfNull(requestStatusIds);
  }

  public Collection<Integer> getInvoiceStatusIds() {
    return toEmptyIfNull(invoiceStatusIds);
  }

  public Collection<Integer> getRequestTypeIds() {
    return toEmptyIfNull(requestTypeIds);
  }

  public Collection<Integer> getProjectClassificationIds() {
    return toEmptyIfNull(projectClassificationIds);
  }

  public Collection<Integer> getRoundOfReviewCounts() {
    return toEmptyIfNull(roundOfReviewCounts);
  }

  public Collection<Integer> getProjectTypeIds() {
    return toEmptyIfNull(projectTypeIds);
  }

  public Optional<ZonedDateTime> getFirstRoundDateStart() {
    return Optional.ofNullable(firstRoundDateStart);
  }

  public Optional<ZonedDateTime> getFirstRoundDateEnd() {
    return Optional.ofNullable(firstRoundDateEnd);
  }

  public Optional<String> getProjectSearch() {
    return Optional.ofNullable(projectSearch);
  }

  private static Collection<Integer> toEmptyIfNull(Collection<Integer> values) {
    return Optional.ofNullable(values).orElse(Collections.emptySet());
  }
}
