package org.goldstandard.assurance.api.repository.assignmentsummary;

import static org.goldstandard.assurance.api.entity.activity.Activity.CONCLUSION_OF_REVIEW_IDS;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.*;
import static org.jooq.impl.DSL.countDistinct;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoeAssignmentSummaryQueryBuilder {
  public static final String QAQC_ASSIGNMENTS = "qaqc_assignments";
  public static final String REQUEST_ID = "request_id";
  public static final String COUNT = "count";
  public static final String ACCOUNT_ID = "account_id";

  private final DSLContext create;
  private final WeekdayCalculator weekdayCalculator;
  private final AuthenticatedUserSupplier userSupplier;
  private final Clock clock;

  public long buildRoeAssignmentsSummaryCount(AssignmentSummaryFiltersDTO filters) {
    return toCount(buildBaseRoeAssignmentsSummary(filters, countDistinct(accountId())));
  }

  public long buildQaqcAssignmentsSummaryCount(AssignmentSummaryFiltersDTO filters) {
    return toCount(buildBaseQaqcRoeAssignmentsSummary(filters, countDistinct(accountId())));
  }

  public Select<Record> buildRoeAssignmentsSummaryQuery(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var fields = merge(roeFields().toArray(new Field[0]), accountName());
    return buildBaseRoeAssignmentsSummary(filters, fields)
        .groupBy(accountName())
        .orderBy(accountName())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());
  }

  public Select<Record> buildQaqcAssignmentsSummaryQuery(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var fields = merge(roeFields().toArray(new Field[0]), accountName());
    return buildBaseQaqcRoeAssignmentsSummary(filters, fields)
        .groupBy(accountName())
        .orderBy(accountName())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());
  }

  public Select<Record> buildRoeAssignmentsSummaryTotalsQuery(AssignmentSummaryFiltersDTO filters) {
    var fields = roeTotalsFields().toArray(new Field[0]);
    return buildBaseRoeAssignmentsSummary(filters, fields);
  }

  public Select<Record> buildQaqcAssignmentsSummaryTotalsQuery(
      AssignmentSummaryFiltersDTO filters) {
    var fields = roeTotalsFields().toArray(new Field[0]);
    return buildBaseQaqcRoeAssignmentsSummary(filters, fields);
  }

  private SelectConditionStep<Record> buildBaseRoeAssignmentsSummary(
      AssignmentSummaryFiltersDTO filters, Field<?>... fields) {
    return buildBaseRoeAssignmentsSummaryQuery(filters, false, fields);
  }

  private SelectConditionStep<Record> buildBaseQaqcRoeAssignmentsSummary(
      AssignmentSummaryFiltersDTO filters, Field<?>... fields) {
    return buildBaseRoeAssignmentsSummaryQuery(filters, true, fields);
  }

  private SelectConditionStep<Record> buildBaseRoeAssignmentsSummaryQuery(
      AssignmentSummaryFiltersDTO filters, boolean includeQaqc, Field<?>... fields) {
    return create
        .select(fields)
        .from(account())
        .leftJoin(closedSubquery(filters, includeQaqc))
        .on(closedSubqueryAccountId().eq(accountId()))
        .leftJoin(openSubquery(filters, includeQaqc))
        .on(openSubqueryAccountId().eq(accountId()))
        .leftJoin(inProgressSubquery(filters, includeQaqc))
        .on(inProgressSubqueryAccountId().eq(accountId()))
        .leftJoin(firstRoundSubquery(filters, includeQaqc))
        .on(firstRoundSubqueryAccountId().eq(accountId()))
        .leftJoin(roeDueSubquery(filters, includeQaqc))
        .on(dueAccountId().eq(accountId()))
        .leftJoin(roeOverdueSubquery(filters, includeQaqc))
        .on(overdueAccountId().eq(accountId()))
        .where(
            closedSubqueryCount()
                .isNotNull()
                .or(openSubqueryCount().isNotNull())
                .or(firstRoundSubqueryCount().isNotNull())
                .or(inProgressSubqueryCount().isNotNull())
                .or(dueCountRoe().isNotNull())
                .or(overdueCountRoe().isNotNull()));
  }

  private Table<?> closedSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    return buildRequestStatusSubquery(
        currentRequestStatusId().in(closedRequestStatuses()), filters, "closed", includeQaqc);
  }

  private Table<?> openSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    return buildRequestStatusSubquery(
        currentRequestStatusId().notIn(closedRequestStatuses()), filters, "open", includeQaqc);
  }

  private Table<?> inProgressSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    return buildRequestStatusSubquery(
        currentRequestStatusId().eq(10), filters, "in_progress", includeQaqc);
  }

  private Table<?> firstRoundSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    return buildRequestStatusSubquery(
        currentRequestStatusId().eq(10).and(requestCurrentRoundIndex().eq(1)),
        filters,
        "first_round",
        includeQaqc);
  }

  private Table<?> buildRequestStatusSubquery(
      Condition statusCondition,
      AssignmentSummaryFiltersDTO filters,
      String tableAlias,
      boolean includeQaqc) {

    var query =
        create
            .select(countDistinct(requestId()).as(COUNT), accountId().as(ACCOUNT_ID))
            .from(requestAccountAssignment())
            .leftJoin(account())
            .on(raaAccountId().eq(accountId()))
            .leftJoin(request())
            .on(raaRequestId().eq(requestId()));

    Condition where = isReviewAssignmentType().and(statusCondition);
    where = addLastSubmittedFilteringIfRequired(where, filters);

    if (includeQaqc) {
      var qaqcWhere = isQaqcAssignmentType();
      qaqcWhere = addLastSubmittedFilteringIfRequired(qaqcWhere, filters);
      var qaqcAssignments = qaqcAssignmentsSubquery(qaqcWhere);

      query = query.leftJoin(qaqcAssignments).on(qaqcAssignedRequesttId().eq(raaRequestId()));
      where = toQaqcAssignmentsSummaryWhereCondition(filters, where);
    }

    return query.where(where).groupBy(accountId()).asTable(tableAlias);
  }

  private Table<?> roeDueSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    var query =
        create
            .select(assignmentAccountId().as(ACCOUNT_ID), countDistinct(raId()).as(COUNT))
            .from(request())
            .leftJoin(requestAccountAssignment())
            .on(requestId().eq(raaRequestId()))
            .leftJoin(requestActivity())
            .on(requestActivityRequestId().eq(requestId()))
            .leftJoin(activity())
            .on(activityId().eq(requestActivityActivityId()));

    Condition where =
        isReviewAssignmentType()
            .and(requestCurrentRequestStatusId().eq(10))
            .and(requestActivityEndDate().isNull())
            .and(activityId().in(convertToParams(CONCLUSION_OF_REVIEW_IDS)));

    where = addRequestDueBeforeFilterIfRequired(filters, where);

    if (includeQaqc) {
      var qaqcWhere = isQaqcAssignmentType();
      qaqcWhere = addLastSubmittedFilteringIfRequired(qaqcWhere, filters);
      var qaqcAssignments = qaqcAssignmentsSubquery(qaqcWhere);

      query = query.leftJoin(qaqcAssignments).on(qaqcAssignedRequesttId().eq(requestId()));
      where = toQaqcAssignmentsSummaryWhereCondition(filters, where);
    }

    return query.where(where).groupBy(assignmentAccountId()).asTable("due");
  }

  private Table<?> roeOverdueSubquery(AssignmentSummaryFiltersDTO filters, boolean includeQaqc) {
    var query =
        create
            .select(assignmentAccountId().as(ACCOUNT_ID), countDistinct(requestId()).as(COUNT))
            .from(request())
            .leftJoin(requestAccountAssignment())
            .on(requestId().eq(raaRequestId()))
            .leftJoin(requestActivity())
            .on(requestActivityRequestId().eq(requestId()))
            .leftJoin(activity())
            .on(activityId().eq(requestActivityActivityId()));

    Condition where =
        requestActivityPlannedEndDate()
            .lessOrEqual(OffsetDateTime.now())
            .and(isReviewAssignmentType())
            .and(requestActivityEndDate().isNull())
            .and(activityId().in(convertToParams(CONCLUSION_OF_REVIEW_IDS)));

    if (includeQaqc) {
      var qaqcWhere = isQaqcAssignmentType();
      qaqcWhere = addLastSubmittedFilteringIfRequired(qaqcWhere, filters);
      var qaqcAssignments = qaqcAssignmentsSubquery(qaqcWhere);

      query = query.leftJoin(qaqcAssignments).on(qaqcAssignedRequesttId().eq(requestId()));
      where = toQaqcAssignmentsSummaryWhereCondition(filters, where);
    }

    return query.where(where).groupBy(assignmentAccountId()).asTable("overdue");
  }

  private Table<?> qaqcAssignmentsSubquery(Condition qaqcWhere) {
    return create
        .select(requestId().as(REQUEST_ID), assignmentAccountId().as("qaqc_assigned_account_id"))
        .from(requestAccountAssignment())
        .leftJoin(request())
        .on(requestId().eq(raaRequestId()))
        .where(qaqcWhere)
        .asTable(QAQC_ASSIGNMENTS);
  }

  private Condition addLastSubmittedFilteringIfRequired(
      Condition where, AssignmentSummaryFiltersDTO filters) {
    where = addLastSubmittedAfterFilterIfRequired(where, filters);
    where = addLastSubmittedBeforeFilterIfRequired(where, filters);
    return where;
  }

  private Condition addLastSubmittedAfterFilterIfRequired(
      Condition where, AssignmentSummaryFiltersDTO filters) {
    return filters
        .getLastSubmittedAfter()
        .map(ZonedDateTime::toOffsetDateTime)
        .map(after -> where.and(lastSubmittedDate().greaterOrEqual(after)))
        .orElse(where);
  }

  private Condition addLastSubmittedBeforeFilterIfRequired(
      Condition where, AssignmentSummaryFiltersDTO filters) {
    return filters
        .getLastSubmittedBefore()
        .map(ZonedDateTime::toOffsetDateTime)
        .map(before -> where.and(lastSubmittedDate().lessOrEqual(before)))
        .orElse(where);
  }

  private Condition addRequestDueBeforeFilterIfRequired(
      AssignmentSummaryFiltersDTO filters, Condition where) {
    var now = ZonedDateTime.now(clock);
    var defaultDueDate = weekdayCalculator.addWeekdays(now, 10).toOffsetDateTime();
    return filters
        .getRequestDueBefore()
        .map(ZonedDateTime::toOffsetDateTime)
        .map(
            before ->
                where.and(
                    requestActivityPlannedEndDate().between(now.toOffsetDateTime()).and(before)))
        .orElse(
            where.and(
                requestActivityPlannedEndDate()
                    .between(now.toOffsetDateTime())
                    .and(defaultDueDate)));
  }

  private Condition toQaqcAssignmentsSummaryWhereCondition(
      AssignmentSummaryFiltersDTO filters, Condition where) {
    if (filters.isQaqcAssignedToCallingAccount()) {
      var userAccountId = userSupplier.forceGetAuthenticatedUserAccountId();
      return where.and(qaqcAssignedAccountId().eq(userAccountId));
    }
    return where;
  }

  private static long toCount(Select<Record> query) {
    return Optional.ofNullable(query.fetchOne(0, Long.class)).orElse(0L);
  }
}
