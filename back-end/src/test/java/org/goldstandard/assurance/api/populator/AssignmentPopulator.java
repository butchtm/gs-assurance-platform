package org.goldstandard.assurance.api.populator;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssignmentPopulator {

  public static final int NUMBER_OF_ASSIGNMENTS = 3;
  public static final ZonedDateTime FIXED_DATE = ZonedDateTime.now();

  @Autowired private RequestAccountAssignmentRepository requestAccountAssignmentRepository;

  public boolean isPopulated() {
    return requestAccountAssignmentRepository.count() > 0;
  }

  public void populate() {
    var assignments = new ArrayList<RequestAccountAssignment>();
    for (int i = 0; i < NUMBER_OF_ASSIGNMENTS; i++) {
      var account = new Account();
      account.setId(1 + i % 2);
      assignments.add(
          RequestAccountAssignment.builder()
              .requestId((i + 2) >> 1)
              .assignmentType(new AssignmentType(i))
              .assignmentStatus(new AssignmentStatus(i % 2))
              .createdTime(FIXED_DATE)
              .account(account)
              .build());
    }

    Iterable<RequestAccountAssignment> savedAssignments =
        requestAccountAssignmentRepository.saveAll(assignments);
    log(savedAssignments);
  }

  public RequestAccountAssignment populate(RequestAccountAssignment assignment) {
    return requestAccountAssignmentRepository.save(assignment);
  }

  public void delete(RequestAccountAssignment... assignments) {
    requestAccountAssignmentRepository.deleteAll(List.of(assignments));
  }

  private static void log(Iterable<RequestAccountAssignment> assignments) {
    assignments.forEach(AssignmentPopulator::log);
  }

  private static void log(RequestAccountAssignment assignment) {
    log.info(
        "populated assignment for request {} to account {} with type {}",
        assignment.getRequestId(),
        assignment.getAccount().getId(),
        assignment.getAssignmentType().getId());
  }
}
