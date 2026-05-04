package org.goldstandard.assurance.api.repository.assignmentsummary;

import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.accountName;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.dueCountArm;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.overdueCountArm;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumClosedCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumDueCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumFirstRoundCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumInProgressCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumOpenCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.sumOverdueCount;
import static org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryQueryComponents.upcomingCount;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummaryTotals;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummaryTotals;
import org.jooq.Record;
import org.springframework.stereotype.Component;

@Component
public class AssignmentSummaryQueryConverter {

  public ArmAssignmentSummary toArmAssignmentSummary(Record r) {
    return ArmAssignmentSummary.builder()
        .accountName(Optional.ofNullable(r.get(accountName())).orElse(""))
        .upcomingCount(safeInt(r.get(upcomingCount())))
        .dueCount(safeInt(r.get(dueCountArm())))
        .overdueCount(safeInt(r.get(overdueCountArm())))
        .build();
  }

  public ArmAssignmentSummaryTotals toArmAssignmentSummaryTotals(Record r) {
    return ArmAssignmentSummaryTotals.builder()
        .upcomingCount(safeInt(r.get(upcomingCount())))
        .dueCount(safeInt(r.get(dueCountArm())))
        .overdueCount(safeInt(r.get(overdueCountArm())))
        .build();
  }

  public RoeAssignmentSummary toAssignmentSummary(Record r) {
    return RoeAssignmentSummary.builder()
        .accountName(r.get(accountName()))
        .dueCount(r.get(sumDueCount()))
        .overdueCount(r.get(sumOverdueCount()))
        .openCount(r.get(sumOpenCount()))
        .closedCount(r.get(sumClosedCount()))
        .inProgressCount(r.get(sumInProgressCount()))
        .firstRoundCount(r.get(sumFirstRoundCount()))
        .build();
  }

  public RoeAssignmentSummaryTotals toAssignmentSummaryTotals(Record r) {
    return RoeAssignmentSummaryTotals.builder()
        .dueCount(r.get(sumDueCount()))
        .overdueCount(r.get(sumOverdueCount()))
        .openCount(r.get(sumOpenCount()))
        .closedCount(r.get(sumClosedCount()))
        .inProgressCount(r.get(sumInProgressCount()))
        .firstRoundCount(r.get(sumFirstRoundCount()))
        .build();
  }

  private static int safeInt(Integer integer) {
    return Optional.ofNullable(integer).orElse(0);
  }
}
