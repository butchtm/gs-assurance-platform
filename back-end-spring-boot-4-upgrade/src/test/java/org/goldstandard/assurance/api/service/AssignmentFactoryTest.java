package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.AssignmentCreateUpdateDTOMother;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentFactoryTest {

  @InjectMocks AssignmentFactory factory;

  @Mock Clock clock;
  @Mock DueDateCalculator dueDateCalculator;
  @Mock AccountConfiguration accountConfiguration;
  @Mock AccountFinder accountFinder;

  @Nested
  class CreateAssignmentTest {

    @Test
    void shouldSetRequestId() {
      mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.build(dto);

      assertThat(assignment.getRequestId()).isEqualTo(dto.getRequestId());
    }

    @Test
    void shouldSetCreatedTime() {
      var now = mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.build(dto);

      assertThat(assignment.getCreatedTime()).isEqualTo(now);
    }

    @Test
    void shouldSetDueDateIfPresent() {
      var now = mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();
      var dueDate = now.plusDays(10);
      when(dueDateCalculator.calculateDueDate(now, dto.getAssignmentTypeId()))
          .thenReturn(Optional.of(dueDate));

      var assignment = factory.build(dto);

      assertThat(assignment.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    void shouldNotSetDueDateIfNotPresent() {
      var now = mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();
      when(dueDateCalculator.calculateDueDate(now, dto.getAssignmentTypeId()))
          .thenReturn(Optional.empty());

      var assignment = factory.build(dto);

      assertThat(assignment.getDueDate()).isNull();
    }

    @Test
    void shouldSetUpdatedTime() {
      var now = mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.build(dto);

      assertThat(assignment.getUpdatedTime()).isEqualTo(now);
    }

    @Test
    void shouldSetAssignmentType() {
      mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.build(dto);

      assertThat(assignment.getAssignmentType().getId()).isEqualTo(dto.getAssignmentTypeId());
    }

    @Test
    void shouldSetAssignmentStatus() {
      mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.build(dto);

      assertThat(assignment.getAssignmentStatus().getId()).isEqualTo(dto.getAssignmentStatusId());
    }

    @Test
    void shouldSetAccount() {
      mockTimeNow();
      var account = AccountMother.any();
      var dto = AssignmentCreateUpdateDTOMother.builder().accountId(account.getId()).build();
      when(accountFinder.forceGetAccountById(account.getId())).thenReturn(account);

      var assignment = factory.build(dto);

      assertThat(assignment.getAccount()).isEqualTo(account);
    }

    @Test
    void shouldSetArmAccountIfAssignmentTypeIsCompletenessCheck() {
      mockTimeNow();
      var armAccount = AccountMother.armAccount();
      when(accountConfiguration.getArmTeamAccountId()).thenReturn(armAccount.getId());
      var dto =
          AssignmentCreateUpdateDTOMother.builder()
              .assignmentTypeId(COMPLETENESS_ASSIGNMENT_TYPE_ID)
              .build();
      when(accountFinder.forceGetAccountById(armAccount.getId())).thenReturn(armAccount);

      var assignment = factory.build(dto);

      assertThat(assignment.getAccount()).isEqualTo(armAccount);
    }
  }

  @Nested
  class UpdateAssignmentTest {

    private final RequestAccountAssignment existingAssignment = new RequestAccountAssignment();

    @Test
    void shouldSetUpdatedTime() {
      var now = mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.applyUpdates(dto, existingAssignment);

      assertThat(assignment.getUpdatedTime()).isEqualTo(now);
    }

    @Test
    void shouldSetAssignmentType() {
      mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.applyUpdates(dto, existingAssignment);

      assertThat(assignment.getAssignmentType().getId()).isEqualTo(dto.getAssignmentTypeId());
    }

    @Test
    void shouldSetAssignmentStatus() {
      mockTimeNow();
      var dto = AssignmentCreateUpdateDTOMother.build();

      var assignment = factory.applyUpdates(dto, existingAssignment);

      assertThat(assignment.getAssignmentStatus().getId()).isEqualTo(dto.getAssignmentStatusId());
    }

    @Test
    void shouldSetAccount() {
      mockTimeNow();
      var account = AccountMother.any();
      var dto = AssignmentCreateUpdateDTOMother.builder().accountId(account.getId()).build();
      when(accountFinder.forceGetAccountById(account.getId())).thenReturn(account);

      var assignment = factory.applyUpdates(dto, existingAssignment);

      assertThat(assignment.getAccount()).isEqualTo(account);
    }

    @Test
    void shouldSetArmAccountIfAssignmentTypeIsCompletenessCheck() {
      mockTimeNow();
      var armAccount = AccountMother.armAccount();
      when(accountConfiguration.getArmTeamAccountId()).thenReturn(armAccount.getId());
      var dto =
          AssignmentCreateUpdateDTOMother.builder()
              .assignmentTypeId(COMPLETENESS_ASSIGNMENT_TYPE_ID)
              .build();
      when(accountFinder.forceGetAccountById(armAccount.getId())).thenReturn(armAccount);

      var assignment = factory.applyUpdates(dto, existingAssignment);

      assertThat(assignment.getAccount()).isEqualTo(armAccount);
    }
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}
