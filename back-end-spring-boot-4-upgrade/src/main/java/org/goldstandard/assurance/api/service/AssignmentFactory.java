package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentFactory {

  private final Clock clock;
  private final DueDateCalculator dueDateCalculator;
  private final AccountConfiguration accountConfiguration;
  private final AccountFinder accountFinder;

  public RequestAccountAssignment build(AssignmentCreateUpdateDTO create) {
    var now = ZonedDateTime.now(clock);
    var builder =
        RequestAccountAssignment.builder().requestId(create.getRequestId()).createdTime(now);
    dueDateCalculator
        .calculateDueDate(now, create.getAssignmentTypeId())
        .ifPresent(builder::dueDate);
    return populateCommonFields(create, builder, now);
  }

  public RequestAccountAssignment applyUpdates(
      AssignmentCreateUpdateDTO update, RequestAccountAssignment assignment) {
    return populateCommonFields(update, assignment.toBuilder(), ZonedDateTime.now(clock));
  }

  private RequestAccountAssignment populateCommonFields(
      AssignmentCreateUpdateDTO update,
      RequestAccountAssignment.RequestAccountAssignmentBuilder builder,
      ZonedDateTime now) {
    builder.updatedTime(now);
    toAssignmentType(update).ifPresent(builder::assignmentType);
    toAssignmentStatus(update).ifPresent(builder::assignmentStatus);
    toAccount(update).ifPresent(builder::account);
    return builder.build();
  }

  private Optional<Account> toAccount(AssignmentCreateUpdateDTO createUpdate) {
    return toAccountId(createUpdate).map(this::toAccount);
  }

  private Optional<Integer> toAccountId(AssignmentCreateUpdateDTO createUpdate) {
    Integer assignmentTypeId = createUpdate.getAssignmentTypeId();
    if (Objects.equals(assignmentTypeId, COMPLETENESS_ASSIGNMENT_TYPE_ID)) {
      return Optional.of(accountConfiguration.getArmTeamAccountId());
    }
    return Optional.ofNullable(createUpdate.getAccountId());
  }

  private Account toAccount(Integer id) {
    return accountFinder.forceGetAccountById(id);
  }

  private static Optional<AssignmentType> toAssignmentType(AssignmentCreateUpdateDTO dto) {
    return Optional.of(dto.getAssignmentTypeId()).map(AssignmentType::new);
  }

  private static Optional<AssignmentStatus> toAssignmentStatus(AssignmentCreateUpdateDTO dto) {
    return Optional.of(dto.getAssignmentStatusId()).map(AssignmentStatus::new);
  }
}
