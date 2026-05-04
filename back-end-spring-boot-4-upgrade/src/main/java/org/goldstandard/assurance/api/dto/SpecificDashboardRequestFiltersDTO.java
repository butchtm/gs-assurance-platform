package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpecificDashboardRequestFiltersDTO {
  private final Collection<Integer> requestStatusIds;
  private final Collection<Integer> projectIds;
  private final Collection<Integer> projectAccountIds;
  private final Collection<Integer> reviewerAccountIds;
  private final Collection<Integer> vvbAccountIds;
  private final Collection<Integer> invoiceStatusIds;
  private final Collection<Integer> activityIds;
  private final Collection<Integer> responsibleAccountIds;
  private final Collection<Integer> requestTypeIds;
  private final Collection<Integer> qaqcAccountIds;
  private final String projectSearch;

  public Collection<Integer> getRequestStatusIds() {
    return toEmptyIfNull(requestStatusIds);
  }

  public Collection<Integer> getProjectIds() {
    return toEmptyIfNull(projectIds);
  }

  public Collection<Integer> getProjectAccountIds() {
    return toEmptyIfNull(projectAccountIds);
  }

  public Collection<Integer> getReviewerAccountIds() {
    return toEmptyIfNull(reviewerAccountIds);
  }

  public Collection<Integer> getVvbAccountIds() {
    return toEmptyIfNull(vvbAccountIds);
  }

  public Collection<Integer> getQaqcAccountIds() {
    return toEmptyIfNull(qaqcAccountIds);
  }

  public Collection<Integer> getInvoiceStatusIds() {
    return toEmptyIfNull(invoiceStatusIds);
  }

  public Collection<Integer> getActivityIds() {
    return toEmptyIfNull(activityIds);
  }

  public Collection<Integer> getResponsibleAccountIds() {
    return toEmptyIfNull(responsibleAccountIds);
  }

  public Collection<Integer> getRequestTypeIds() {
    return toEmptyIfNull(requestTypeIds);
  }

  public Optional<String> getProjectSearch() {
    return Optional.ofNullable(projectSearch);
  }

  private static Collection<Integer> toEmptyIfNull(Collection<Integer> values) {
    return Optional.ofNullable(values).orElse(Collections.emptySet());
  }
}
