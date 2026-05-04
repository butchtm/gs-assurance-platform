package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentFinderTest {

  @InjectMocks AssignmentFinder finder;

  @Mock RequestAccountAssignmentRepository repository;

  @Test
  void shouldReturnAssignmentByIdIfExists() {
    var expectedAssignment =
        RequestAccountAssignmentMother.builder()
            .requestId(3)
            .account(AccountMother.withIdOnly(4))
            .build();
    when(repository.findById(expectedAssignment.getId()))
        .thenReturn(Optional.of(expectedAssignment));

    var assignment = finder.findByAssignmentId(expectedAssignment.getId());

    assertThat(assignment).contains(expectedAssignment);
  }

  @Test
  void shouldReturnEmptyIfAssignmentByIdDoesNotExist() {
    var assignmentId = 5;
    when(repository.findById(assignmentId)).thenReturn(Optional.empty());

    var assignment = finder.findByAssignmentId(assignmentId);

    assertThat(assignment).isEmpty();
  }

  @Test
  void forceFindAssignmentByIdShouldReturnAssignmentIfExists() {
    var expectedAssignment =
        RequestAccountAssignmentMother.builder()
            .requestId(6)
            .account(AccountMother.withIdOnly(7))
            .build();
    when(repository.findById(expectedAssignment.getId()))
        .thenReturn(Optional.of(expectedAssignment));

    var assignment = finder.forceFindAssignmentById(expectedAssignment.getId());

    assertThat(assignment).isEqualTo(expectedAssignment);
  }

  @Test
  void forceFindAssignmentByIdShouldThrowExceptionIfDoesNotExist() {
    var assignmentId = 8;
    when(repository.findById(assignmentId)).thenReturn(Optional.empty());

    var error = catchThrowable(() -> finder.forceFindAssignmentById(assignmentId));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("assignment with id %s not found", assignmentId);
  }
}
