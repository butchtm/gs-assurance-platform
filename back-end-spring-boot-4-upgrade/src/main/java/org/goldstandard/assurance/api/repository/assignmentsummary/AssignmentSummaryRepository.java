package org.goldstandard.assurance.api.repository.assignmentsummary;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummaryTotals;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummaryTotals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentSummaryRepository {

  private final ArmAssignmentSummaryQueryBuilder armQueryBuilder;
  private final RoeAssignmentSummaryQueryBuilder roeQueryBuilder;
  private final AssignmentSummaryQueryConverter converter;

  public Page<ArmAssignmentSummary> getArmAssignmentSummary(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var totalCount = armQueryBuilder.buildArmAssignmentsSummaryCount(filters);
    var query = armQueryBuilder.buildArmAssignmentsSummaryQuery(filters, pageable);
    var items = query.fetch(converter::toArmAssignmentSummary);
    return new PageImpl<>(items, pageable, totalCount);
  }

  public Optional<ArmAssignmentSummaryTotals> getArmAssignmentSummaryTotals(
      AssignmentSummaryFiltersDTO filters) {
    var query = armQueryBuilder.buildArmAssignmentsSummaryTotalsQuery(filters);
    return Optional.ofNullable(query.fetchOne(converter::toArmAssignmentSummaryTotals));
  }

  public Page<RoeAssignmentSummary> getReviewAssignmentSummary(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    if (filters.isQaqcAssignedToCallingAccount()) {
      return getQaqcAssignmentSummary(filters, pageable);
    }
    return getDefaultAssignmentSummary(filters, pageable);
  }

  public Optional<RoeAssignmentSummaryTotals> getReviewAssignmentSummaryTotals(
      AssignmentSummaryFiltersDTO filters) {
    if (filters.isQaqcAssignedToCallingAccount()) {
      return getQaqcAssignmentSummaryTotals(filters);
    }
    return getDefaultAssignmentSummaryTotals(filters);
  }

  private Page<RoeAssignmentSummary> getDefaultAssignmentSummary(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var totalCount = roeQueryBuilder.buildRoeAssignmentsSummaryCount(filters);
    var query = roeQueryBuilder.buildRoeAssignmentsSummaryQuery(filters, pageable);
    var items = query.fetch(converter::toAssignmentSummary);
    return new PageImpl<>(items, pageable, totalCount);
  }

  private Optional<RoeAssignmentSummaryTotals> getDefaultAssignmentSummaryTotals(
      AssignmentSummaryFiltersDTO filters) {
    var query = roeQueryBuilder.buildRoeAssignmentsSummaryTotalsQuery(filters);
    return Optional.ofNullable(query.fetchOne(converter::toAssignmentSummaryTotals));
  }

  private Page<RoeAssignmentSummary> getQaqcAssignmentSummary(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var totalCount = roeQueryBuilder.buildQaqcAssignmentsSummaryCount(filters);
    var query = roeQueryBuilder.buildQaqcAssignmentsSummaryQuery(filters, pageable);
    var items = query.fetch(converter::toAssignmentSummary);
    return new PageImpl<>(items, pageable, totalCount);
  }

  private Optional<RoeAssignmentSummaryTotals> getQaqcAssignmentSummaryTotals(
      AssignmentSummaryFiltersDTO filters) {
    var query = roeQueryBuilder.buildQaqcAssignmentsSummaryTotalsQuery(filters);
    return Optional.ofNullable(query.fetchOne(converter::toAssignmentSummaryTotals));
  }
}
