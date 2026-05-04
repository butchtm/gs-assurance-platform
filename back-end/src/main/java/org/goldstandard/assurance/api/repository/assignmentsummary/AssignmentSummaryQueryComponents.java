package org.goldstandard.assurance.api.repository.assignmentsummary;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.QAQC_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.REVIEW_ASSIGNMENT_TYPE_ID;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.table;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Component
public class AssignmentSummaryQueryComponents {

  public static Table<Record> account() {
    return table("gs.account a");
  }

  public static Table<Record> activity() {
    return table("gs.activity a");
  }

  public static Table<Record> request() {
    return table("gs.request r");
  }

  public static Table<Record> requestAccountAssignment() {
    return table("gs.request_account_assignment raa");
  }

  public static Table<Record> raaArm() {
    return table("gs.request_account_assignment raa_arm");
  }

  public static Table<Record> raaReview() {
    return table("gs.request_account_assignment raa_review");
  }

  public static Table<Record> requestActivity() {
    return table("gs.request_activity ra");
  }

  public static List<Field<Integer>> armFields() {
    return List.of(upcomingCount(), dueCountArm(), overdueCountArm());
  }

  public static List<Field<Integer>> sumArmFields() {
    return List.of(upcomingCountSum(), dueCountSum(), overdueCountSum());
  }

  public static List<Field<Integer>> roeFields() {
    return List.of(
        sumClosedCount(),
        sumOpenCount(),
        sumInProgressCount(),
        sumFirstRoundCount(),
        sumDueCount(),
        sumOverdueCount());
  }

  public static List<Field<Integer>> roeTotalsFields() {
    return List.of(
        sumClosedCount(),
        sumOpenCount(),
        sumInProgressCount(),
        sumFirstRoundCount(),
        sumDueCount(),
        sumOverdueCount());
  }

  public static Field<Integer> accountId() {
    return field("a.id", Integer.class);
  }

  public static Field<String> accountName() {
    return field("a.name", String.class);
  }

  public static Field<Integer> activityId() {
    return field("a.id", Integer.class);
  }

  public static Field<Integer> requestId() {
    return field("r.id", Integer.class);
  }

  public static Field<Integer> currentRequestStatusId() {
    return field("r.current_request_status_id", Integer.class);
  }

  public static Field<Integer> requestCurrentRequestStatusId() {
    return field("r.current_request_status_id", Integer.class);
  }

  public static Field<Integer> requestCurrentRoundIndex() {
    return field("r.current_round_index", Integer.class);
  }

  public static Field<OffsetDateTime> lastSubmittedDate() {
    return field("r.last_request_status_change_date", OffsetDateTime.class);
  }

  public static Field<Integer> raaAccountId() {
    return field("raa.account_id", Integer.class);
  }

  public static Field<Integer> assignmentAccountId() {
    return field("raa.account_id", Integer.class);
  }

  public static Field<Integer> raaRequestId() {
    return field("raa.request_id", Integer.class);
  }

  public static Field<Integer> raaAssignmentTypeId() {
    return field("raa.assignment_type_id", Integer.class);
  }

  public static Field<Integer> assignmentTypeId() {
    return field("raa.assignment_type_id", Integer.class);
  }

  public static Field<Integer> raId() {
    return field("ra.id", Integer.class);
  }

  public static Field<Integer> requestActivityRequestId() {
    return field("ra.request_id", Integer.class);
  }

  public static Field<Integer> requestActivityActivityId() {
    return field("ra.activity_id", Integer.class);
  }

  public static Field<OffsetDateTime> requestActivityPlannedEndDate() {
    return field("ra.planned_end_date", OffsetDateTime.class);
  }

  public static Field<OffsetDateTime> plannedEndDate() {
    return field("ra.planned_end_date", OffsetDateTime.class);
  }

  public static Field<OffsetDateTime> requestActivityEndDate() {
    return field("ra.end_date", OffsetDateTime.class);
  }

  public static Field<OffsetDateTime> actualEndDate() {
    return field("ra.end_date", OffsetDateTime.class);
  }

  public static Field<Integer> qaqcAssignedAccountId() {
    return field("qaqc_assignments.qaqc_assigned_account_id", Integer.class);
  }

  public static Field<Integer> qaqcAssignedRequesttId() {
    return field("qaqc_assignments.request_id", Integer.class);
  }

  public static Field<Integer> closedSubqueryCount() {
    return field("closed.count", Integer.class);
  }

  public static Field<Integer> closedSubqueryAccountId() {
    return field("closed.account_id", Integer.class);
  }

  public static Field<Integer> openSubqueryCount() {
    return field("open.count", Integer.class);
  }

  public static Field<Integer> openSubqueryAccountId() {
    return field("open.account_id", Integer.class);
  }

  public static Field<Integer> inProgressSubqueryCount() {
    return field("in_progress.count", Integer.class);
  }

  public static Field<Integer> inProgressSubqueryAccountId() {
    return field("in_progress.account_id", Integer.class);
  }

  public static Field<Integer> firstRoundSubqueryCount() {
    return field("first_round.count", Integer.class);
  }

  public static Field<Integer> firstRoundSubqueryAccountId() {
    return field("first_round.account_id", Integer.class);
  }

  public static Field<Integer> dueCountRoe() {
    return field("due.count", Integer.class);
  }

  public static Field<Integer> dueAccountId() {
    return field("due.account_id", Integer.class);
  }

  public static Field<Integer> overdueCountRoe() {
    return field("overdue.count", Integer.class);
  }

  public static Field<Integer> overdueAccountId() {
    return field("overdue.account_id", Integer.class);
  }

  public static Field<Integer> sumClosedCount() {
    return coalesce(sum(closedSubqueryCount()), inline(0)).cast(Integer.class).as("closed_count");
  }

  public static Field<Integer> sumOpenCount() {
    return coalesce(sum(openSubqueryCount()), inline(0)).cast(Integer.class).as("open_count");
  }

  public static Field<Integer> sumInProgressCount() {
    return coalesce(sum(inProgressSubqueryCount()), inline(0))
        .cast(Integer.class)
        .as("in_progress_count");
  }

  public static Field<Integer> sumFirstRoundCount() {
    return coalesce(sum(firstRoundSubqueryCount()), inline(0))
        .cast(Integer.class)
        .as("first_round_count");
  }

  public static Field<Integer> sumDueCount() {
    return coalesce(sum(dueCountRoe()), inline(0)).cast(Integer.class).as("due_count");
  }

  public static Field<Integer> sumOverdueCount() {
    return coalesce(sum(overdueCountRoe()), inline(0)).cast(Integer.class).as("overdue_count");
  }

  public static Field<Integer> upcomingCountSum() {
    return sum(upcomingCoalesce()).cast(Integer.class).as("upcoming");
  }

  public static Field<Integer> upcomingCount() {
    return upcomingCoalesce().as("upcoming");
  }

  private static Field<Integer> upcomingCoalesce() {
    return coalesce(field("review_assignments.count", Integer.class), inline(0));
  }

  public static Field<Integer> dueCountSum() {
    return sum(dueCoalesceArm()).cast(Integer.class).as("due");
  }

  public static Field<Integer> dueCountArm() {
    return dueCoalesceArm().as("due");
  }

  private static Field<Integer> dueCoalesceArm() {
    return coalesce(field("arm_due.count", Integer.class), inline(0));
  }

  public static Field<Integer> overdueCountSum() {
    return sum(overdueCoalesceArm()).cast(Integer.class).as("overdue");
  }

  public static Field<Integer> overdueCountArm() {
    return overdueCoalesceArm().as("overdue");
  }

  private static Field<Integer> overdueCoalesceArm() {
    return coalesce(field("arm_overdue.count", Integer.class), inline(0));
  }

  public static Field<Integer> projectId() {
    return field("p.id", Integer.class);
  }

  public static Field<Integer> projectTypeId() {
    return field("ppt.project_type_id", Integer.class);
  }

  public static Table<Record> baseProject() {
    return table("gs.base_project_request bpr");
  }

  public static Table<Record> projectType() {
    return table("gs.project_project_type ppt");
  }

  public static List<Field<Integer>> closedRequestStatuses() {
    return List.of(inline(4), inline(5), inline(7), inline(8));
  }

  public static Condition isInProgress() {
    return currentRequestStatusId().eq(inline(10));
  }

  public static Condition inRoundCompletion() {
    return currentRequestStatusId().eq(inline(13));
  }

  public static Condition isQaqcAssignmentType() {
    return raaAssignmentTypeId().eq(inline(QAQC_ASSIGNMENT_TYPE_ID));
  }

  public static Condition isReviewAssignmentType() {
    return raaAssignmentTypeId().eq(inline(REVIEW_ASSIGNMENT_TYPE_ID));
  }

  @SuppressWarnings("java:S1452")
  public static Field<?>[] merge(Field<?>[] f1, Field<?>... f2) {
    return Stream.concat(Stream.of(f1), Stream.of(f2)).toArray(Field<?>[]::new);
  }

  public static Collection<Param<Integer>> convertToParams(Collection<Integer> integers) {
    return integers.stream().map(DSL::inline).toList();
  }
}
