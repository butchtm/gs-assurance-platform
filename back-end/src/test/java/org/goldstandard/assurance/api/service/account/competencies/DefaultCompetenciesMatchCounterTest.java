package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.function.ToLongFunction;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCompetenciesMatchCounterTest {

  @InjectMocks DefaultCompetenciesMatchCounter defaultMatchCounter;

  @Mock ToLongFunction<Request> matchCounter;

  @Mock Competencies competencies;

  @Mock AssignmentFinder assignmentFinder;

  @Test
  void shouldReturnDefaultCountIfGSIDDoesNotMatchAndAssignedVvbDoesNotMatch() {
    long expectedDefaultCount = 0;
    var request = new Request();
    request.setProject(new Project());
    when(competencies.getProjectGsids()).thenReturn(Collections.emptyList());
    when(matchCounter.applyAsLong(request)).thenReturn(expectedDefaultCount);

    long count = defaultMatchCounter.applyAsLong(request);

    assertThat(count).isEqualTo(expectedDefaultCount);
  }

  @Test
  void shouldIncrementCountIfGSIDMatches() {
    long expectedDefaultCount = 0;
    String gsid = "GS123";
    var project = new Project();
    project.setGsid(gsid);
    var request = toRequest(project);
    when(competencies.getProjectGsids()).thenReturn(List.of(gsid));
    when(matchCounter.applyAsLong(request)).thenReturn(expectedDefaultCount);

    long count = defaultMatchCounter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldIncrementCountIfVvbAssignmentContainsMatchingAccount() {
    long expectedDefaultCount = 0;
    var account = new Account(1);
    var request = new Request();
    request.setProject(new Project());
    when(assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()))
        .thenReturn(List.of(account));
    when(competencies.getVvbsAssigned()).thenReturn(List.of(account));
    when(matchCounter.applyAsLong(request)).thenReturn(expectedDefaultCount);

    long count = defaultMatchCounter.applyAsLong(request);

    assertThat(count).isOne();
  }

  private static Request toRequest(Project project) {
    var request = new Request();
    request.setProject(project);
    return request;
  }
}
