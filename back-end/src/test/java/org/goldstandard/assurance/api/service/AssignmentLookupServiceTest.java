package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentLookupServiceTest {

  @InjectMocks AssignmentLookupService assignmentLookupService;

  @Mock AssignmentFinder assignmentFinder;

  @Test
  void shouldLookupAssignments() {
    var request = Request.builder().id(989).build();
    var expectedAssignments = List.of(mock(RequestAccountAssignment.class));
    when(assignmentFinder.findAll(Mockito.any())).thenReturn(expectedAssignments);

    var assignments = assignmentLookupService.findAssignments(1, request);

    assertThat(assignments).isEqualTo(expectedAssignments);
  }
}
