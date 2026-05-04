package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectCreditingPeriodUpdatePolicyTest {

  @InjectMocks ProjectCreditingPeriodUpdatePolicy policy;

  @Mock ProjectCreditingPeriodUpdateApplier applier;

  @Test
  void shouldNotUpdateIfRequestAlreadyComplete() {
    var originalRequests = List.of(givenRequestWithCompletedStatus().build());
    var requestMethodology = mock(RequestMethodology.class);
    var newMonitoringPeriod = mock(RequestMonitoringPeriod.class);
    var updated =
        givenRequestWithCompletedStatus()
            .requestMethodologies(List.of(requestMethodology))
            .draftMonitoringPeriod(newMonitoringPeriod)
            .build();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(originalRequests, List.of(updated), projects);

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @Test
  void shouldNotUpdateIfRequestNotMovedToCompletedApproved() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var newMonitoringPeriod = mock(RequestMonitoringPeriod.class);
    var updated =
        givenRequestWithIncompleteStatus()
            .requestMethodologies(List.of(mock(RequestMethodology.class)))
            .draftMonitoringPeriod(newMonitoringPeriod)
            .build();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(originalRequests, List.of(updated), projects);

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @Test
  void shouldNotUpdateIfRequestHasNoMethodologies() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var updated =
        givenRequestWithCompletedStatus().requestMethodologies(Collections.emptyList()).build();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(originalRequests, List.of(updated), projects);

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @Test
  void shouldNotUpdateIfRequestTypeNotAllowedMethodologies() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var requestType = mock(RequestType.class);
    when(requestType.allowsMethodologies()).thenReturn(false);
    var updatedRequest =
        givenRequestWithCompletedStatus()
            .creditingPeriod(1)
            .requestMethodologies(List.of(mock(RequestMethodology.class)))
            .requestType(requestType)
            .build();
    var originalProject = buildProject();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(
            originalRequests, List.of(updatedRequest), List.of(originalProject));

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @ParameterizedTest
  @MethodSource("terminalStatus")
  void shouldUpdateIfRequestHasMethodologiesAndMovedToCompletedAndRequestTypeAllowsMethodologies(
      Request.RequestBuilder requestWithTerminalState) {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var requestType = givenRequestTypeAllowsMethodologies();
    var updatedRequest =
        requestWithTerminalState
            .creditingPeriod(1)
            .requestMethodologies(List.of(mock(RequestMethodology.class)))
            .requestType(requestType)
            .build();
    var originalProject = buildProject();
    var updatedProject = buildProject();
    when(applier.updateProjectMethodologies(originalProject.getCreditingPeriods(), updatedRequest))
        .thenReturn(updatedProject.getCreditingPeriods());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(
            originalRequests, List.of(updatedRequest), List.of(originalProject));

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(originalProject);
  }

  @Test
  void shouldNotUpdateMonitoringPeriodsIfRequestDoesNotHaveNewMonitoringPeriodDates() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var newMonitoringPeriod = mock(RequestMonitoringPeriod.class);
    when(newMonitoringPeriod.getPeriodId()).thenReturn(null);
    var updated =
        givenRequestWithCompletedStatus()
            .requestType(new RequestType(COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA))
            .requestMethodologies(Collections.emptyList())
            .draftMonitoringPeriod(newMonitoringPeriod)
            .build();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(originalRequests, List.of(updated), projects);
    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @Test
  void shouldNotUpdateMonitoringPeriodsIfRequestTypeNotAllowedCreateMonitoringPeriod() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var requestType = mock(RequestType.class);
    when(requestType.allowsCreateMonitoringPeriod()).thenReturn(false);
    var newMonitoringPeriod = givenNewMonitoringPeriod();
    var updatedRequest =
        givenRequestWithCompletedStatus()
            .creditingPeriod(1)
            .requestMethodologies(Collections.emptyList())
            .draftMonitoringPeriod(newMonitoringPeriod)
            .requestType(requestType)
            .build();
    var originalProject = buildProject();
    var projects = List.of(buildProject());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(
            originalRequests, List.of(updatedRequest), List.of(originalProject));

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(projects);
  }

  @Test
  void shouldUpdateMonitoringPeriodsIfRequestHasMonitoringPeriodDatesAndRequestTypeAllowed() {
    var originalRequests = List.of(givenRequestWithIncompleteStatus().build());
    var requestType = mock(RequestType.class);
    when(requestType.allowsCreateMonitoringPeriod()).thenReturn(true);
    var newMonitoringPeriod = givenNewMonitoringPeriod();
    var updatedRequest =
        givenRequestWithCompletedStatus()
            .creditingPeriod(1)
            .requestMethodologies(Collections.emptyList())
            .draftMonitoringPeriod(newMonitoringPeriod)
            .requestType(requestType)
            .build();
    var originalProject = buildProject();
    var updatedProject = buildProject();
    when(applier.updateMonitoringPeriods(originalProject.getCreditingPeriods(), updatedRequest))
        .thenReturn(updatedProject.getCreditingPeriods().stream().toList());

    var updatedProjects =
        policy.calculateProjectCreditingPeriodUpdates(
            originalRequests, List.of(updatedRequest), List.of(originalProject));

    assertThat(updatedProjects)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(originalProject);
  }

  private static RequestType givenRequestTypeAllowsMethodologies() {
    var requestType = mock(RequestType.class);
    when(requestType.allowsMethodologies()).thenReturn(true);
    return requestType;
  }

  private static Stream<Arguments> terminalStatus() {
    return Stream.of(
        Arguments.of(givenRequestWithCompletedStatus()),
        Arguments.of(givenRequestWithRejectedStatus()));
  }

  private static Request.RequestBuilder givenRequestWithCompletedStatus() {
    return requestBuilder().currentRequestStatus(RequestStatus.completedApproved());
  }

  private static Request.RequestBuilder givenRequestWithRejectedStatus() {
    return requestBuilder().currentRequestStatus(RequestStatus.completedRejected());
  }

  private static Request.RequestBuilder givenRequestWithIncompleteStatus() {
    return requestBuilder().currentRequestStatus(RequestStatus.inPreparation());
  }

  private static RequestMonitoringPeriod givenNewMonitoringPeriod() {
    var monitoringPeriod = mock(RequestMonitoringPeriod.class);
    var now = ZonedDateTime.now();
    when(monitoringPeriod.getStartDate()).thenReturn(now);
    when(monitoringPeriod.getEndDate()).thenReturn(now.plusYears(1));
    return monitoringPeriod;
  }

  private static Request.RequestBuilder requestBuilder() {
    return Request.builder().id(1004).project(buildProject());
  }

  private static Project buildProject() {
    return Project.builder()
        .id(999)
        .creditingPeriods(List.of(mock(ProjectCreditingPeriod.class)))
        .build();
  }
}
