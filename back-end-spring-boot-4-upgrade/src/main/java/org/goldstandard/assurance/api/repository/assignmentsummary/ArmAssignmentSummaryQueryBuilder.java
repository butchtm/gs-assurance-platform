package org.goldstandard.assurance.api.repository.assignmentsummary;

import static org.goldstandard.assurance.api.entity.activity.Activity.CONCLUSION_OF_REVIEW_IDS;
import static org.goldstandard.assurance.api.entity.activity.Activity.HARMONISATION_OF_FEEDBACK_IDS;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.account;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.accountId;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.accountName;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.activity;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.actualEndDate;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.armFields;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.assignmentAccountId;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.baseProject;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.convertToParams;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.inRoundCompletion;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.isInProgress;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.isQaqcAssignmentType;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.merge;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.plannedEndDate;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.projectType;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.projectTypeId;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.raaArm;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.raaReview;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.request;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.requestAccountAssignment;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.requestActivity;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumArmFields;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.noCondition;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
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
@Slf4j
public class ArmAssignmentSummaryQueryBuilder {
  private final DSLContext create;
  private final WeekdayCalculator weekdayCalculator;

  public long buildArmAssignmentsSummaryCount(AssignmentSummaryFiltersDTO filters) {
    return toCount(buildBaseArmAssignmentsSummary(filters, countDistinct(accountId())));
  }

  public Select<Record> buildArmAssignmentsSummaryQuery(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    var fields = merge(armFields().toArray(new Field[0]), accountId(), accountName());
    return buildBaseArmAssignmentsSummary(filters, fields)
        .orderBy(accountName())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());
  }

  public Select<Record> buildArmAssignmentsSummaryTotalsQuery(AssignmentSummaryFiltersDTO filters) {
    var fields = sumArmFields().toArray(new Field[0]);
    return buildBaseArmAssignmentsSummary(filters, fields);
  }

  private SelectConditionStep<Record> buildBaseArmAssignmentsSummary(
      AssignmentSummaryFiltersDTO filters, Field<?>... fields) {
    return create
        .select(fields)
        .from(account())
        .leftJoin(reviewerRoundCompletionUpcomingSubquery(filters))
        .on("review_assignments.account_id = a.id")
        .leftJoin(armAssignmentRoundCompletionDueSubquery(filters))
        .on("arm_due.account_id = a.id")
        .leftJoin(armAssignmentRoundCompletionOverdueSubquery(filters))
        .on("arm_overdue.account_id = a.id")
        .where(
            field("review_assignments.count")
                .isNotNull()
                .or(field("arm_due.count").isNotNull())
                .or(field("arm_overdue.count").isNotNull()));
  }

  private Condition addRequestDueBetweenFilterIfRequired(
      Condition where, AssignmentSummaryFiltersDTO filters) {
    var now = ZonedDateTime.now();
    return filters
        .getRequestDueBefore()
        .map(ZonedDateTime::toOffsetDateTime)
        .map(dateTime -> where.and(plannedEndDate().between(now.toOffsetDateTime(), dateTime)))
        .orElse(
            where.and(
                plannedEndDate()
                    .lessOrEqual(weekdayCalculator.addWeekdays(now, 10).toOffsetDateTime())));
  }

  private static long toCount(Select<Record> query) {
    return Optional.ofNullable(query.fetchOne(0, Long.class)).orElse(0L);
  }

  private Table<?> reviewerRoundCompletionUpcomingSubquery(AssignmentSummaryFiltersDTO filters) {
    var where =
        isInProgress()
            .and(field("a.id").in(convertToParams(CONCLUSION_OF_REVIEW_IDS)))
            .and(actualEndDate().isNull());
    where =
        addRequestDueBetweenFilterIfRequired(where, filters)
            .and(projectAndRequestTypeFilters(filters));

    return create
        .select(
            field("raa_arm.account_id").as("account_id"), countDistinct(field("r.id")).as("count"))
        .from(request())
        .innerJoin(requestActivity())
        .on("ra.request_id = r.id")
        .innerJoin(activity())
        .on("a.id = ra.activity_id")
        .innerJoin(raaReview())
        .on("raa_review.request_id = r.id and raa_review.assignment_type_id = 1")
        .innerJoin(raaArm())
        .on("raa_arm.request_id = r.id and raa_arm.assignment_type_id = 2")
        .innerJoin(baseProject())
        .on("bpr.request_id = r.id")
        .where(where)
        .groupBy(field("raa_arm.account_id"))
        .asTable("review_assignments");
  }

  private Table<?> armAssignmentRoundCompletionDueSubquery(AssignmentSummaryFiltersDTO filters) {
    var where = addRequestDueBetweenFilterIfRequired(noCondition(), filters);
    return buildArmAssignmentSubquery(where, "ra.id", "arm_due", filters);
  }

  private Table<?> armAssignmentRoundCompletionOverdueSubquery(
      AssignmentSummaryFiltersDTO filters) {
    var where = plannedEndDate().lessOrEqual(ZonedDateTime.now().toOffsetDateTime());
    return buildArmAssignmentSubquery(where, "r.id", "arm_overdue", filters);
  }

  private Table<?> buildArmAssignmentSubquery(
      Condition where, String countField, String tableAlias, AssignmentSummaryFiltersDTO filters) {
    where =
        where
            .and(isQaqcAssignmentType())
            .and(inRoundCompletion())
            .and(actualEndDate().isNull())
            .and(field("a.id").in(convertToParams(HARMONISATION_OF_FEEDBACK_IDS)))
            .and(projectAndRequestTypeFilters(filters));
    return create
        .select(
            assignmentAccountId().as("account_id"), countDistinct(field(countField)).as("count"))
        .from(request())
        .leftJoin(requestAccountAssignment())
        .on("r.id = raa.request_id")
        .leftJoin(requestActivity())
        .on("ra.request_id = r.id")
        .leftJoin(activity())
        .on("a.id = ra.activity_id")
        .innerJoin(baseProject())
        .on("bpr.request_id = r.id")
        .where(where)
        .groupBy(assignmentAccountId())
        .asTable(tableAlias);
  }

  private Condition projectAndRequestTypeFilters(AssignmentSummaryFiltersDTO filters) {
    return projectTypeFilter(filters).and(requestTypeFilter(filters));
  }

  private Condition projectTypeFilter(AssignmentSummaryFiltersDTO filters) {
    return isEmpty(filters.getProjectTypeIds())
        ? noCondition()
        : field("bpr.project_id")
            .in(
                create
                    .select(field("ppt.project_id"))
                    .from(projectType())
                    .where(projectTypeId().in(filters.getProjectTypeIds()))
                    .fetchInto(Integer.class));
  }

  private Condition requestTypeFilter(AssignmentSummaryFiltersDTO filters) {
    return isEmpty(filters.getRequestTypeIds())
        ? noCondition()
        : field("r.request_type_id").in(filters.getRequestTypeIds());
  }
}
