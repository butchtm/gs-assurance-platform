package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment.RequestAccountAssignmentBuilder;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestAccountAssignmentMother {

  public static RequestAccountAssignment armCompletenessCheckAssignment(int requestId) {
    return builder()
        .account(AccountMother.armAccount())
        .assignmentType(completenessCheck())
        .requestId(requestId)
        .build();
  }

  public static RequestAccountAssignment completenessCheckAssignment(int requestId, int accountId) {
    return builder()
        .account(AccountMother.withIdOnly(accountId))
        .assignmentType(completenessCheck())
        .requestId(requestId)
        .build();
  }

  public static RequestAccountAssignment build() {
    return builder().build();
  }

  public static RequestAccountAssignmentBuilder builder() {
    var now = ZonedDateTimeMother.now();
    return RequestAccountAssignment.builder()
        .createdTime(now)
        .updatedTime(now)
        .requestId(1)
        .account(AccountMother.armAccount())
        .assignmentType(new AssignmentType(0))
        .assignmentStatus(new AssignmentStatus(0))
        .dueDate(now.plusDays(7));
  }

  private static AssignmentType completenessCheck() {
    return new AssignmentType(COMPLETENESS_ASSIGNMENT_TYPE_ID);
  }
}
