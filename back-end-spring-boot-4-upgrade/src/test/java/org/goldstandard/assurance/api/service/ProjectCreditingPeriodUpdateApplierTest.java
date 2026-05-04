package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectCreditingPeriodUpdateApplierTest {

  public static final int REQUEST_ID = 1001;

  @InjectMocks ProjectCreditingPeriodUpdateApplier applier;

  @Mock CreditingPeriodFactory creditingPeriodFactory;

  @Mock ProjectCreditingPeriod projectCreditingPeriod;

  @Mock Clock clock;

  @Test
  void shouldUpdateProjectMethodologies() {
    var updatedRequest = Request.builder().id(REQUEST_ID).build();
    var creditingPeriod =
        ProjectCreditingPeriod.builder()
            .projectId(1)
            .periodId(2)
            .methodologies(List.of(buildProjectCreditingPeriodMethodology(100, 1.0)))
            .build();
    var creditingPeriods = List.of(creditingPeriod);
    var requestMethodologies =
        List.of(
            buildRequestMethodology(updatedRequest, 100, 1.1),
            buildRequestMethodology(updatedRequest, 101, 1.0));
    updatedRequest.setRequestMethodologies(requestMethodologies);
    updatedRequest.setCreditingPeriod(creditingPeriod.getPeriodId());
    var expectedProjectCreditingPeriod =
        ProjectCreditingPeriod.builder()
            .methodologies(
                List.of(
                    buildProjectCreditingPeriodMethodology(100, 1.1),
                    buildProjectCreditingPeriodMethodology(101, 1.0)))
            .build();
    when(creditingPeriodFactory.toProjectCreditingPeriodMethodologies(
            creditingPeriod, requestMethodologies))
        .thenReturn(expectedProjectCreditingPeriod.getMethodologies());

    var updatedCreditingPeriods =
        applier.updateProjectMethodologies(creditingPeriods, updatedRequest);

    var updatedMethodologies =
        updatedCreditingPeriods.stream().flatMap(cp -> cp.getMethodologies().stream()).toList();
    assertThat(updatedMethodologies).isEqualTo(expectedProjectCreditingPeriod.getMethodologies());
  }

  @Test
  void shouldUpdateMonitoringPeriods() {
    var startDate = ZonedDateTime.now();
    var endDate = startDate.plusYears(1);
    var newMonitoringPeriod =
        RequestMonitoringPeriod.builder().startDate(startDate).endDate(endDate).build();
    var updatedRequest =
        Request.builder().id(REQUEST_ID).draftMonitoringPeriod(newMonitoringPeriod).build();
    var creditingPeriod1 =
        ProjectCreditingPeriod.builder()
            .projectId(1)
            .periodId(1)
            .monitoringPeriods(Collections.emptyList())
            .build();
    var creditingPeriod2 = ProjectCreditingPeriod.builder().projectId(1).periodId(2).build();
    var creditingPeriods = List.of(creditingPeriod1, creditingPeriod2);
    var expectedMonitoringPeriod =
        ProjectCreditingMonitoringPeriod.builder()
            .id(3)
            .projectId(1)
            .periodId(2)
            .startDate(startDate)
            .endDate(endDate)
            .originRequestId(REQUEST_ID)
            .build();
    var expectedCreditingPeriod =
        creditingPeriod2.withMonitoringPeriods(List.of(expectedMonitoringPeriod));
    updatedRequest.setCreditingPeriod(creditingPeriod2.getPeriodId());
    when(creditingPeriodFactory.addMonitoringPeriod(
            creditingPeriod2, updatedRequest.getId(), newMonitoringPeriod))
        .thenReturn(expectedCreditingPeriod);

    var updatedCreditingPeriods = applier.updateMonitoringPeriods(creditingPeriods, updatedRequest);

    var updatedMonitoringPeriods =
        updatedCreditingPeriods.stream()
            .flatMap(period -> period.getMonitoringPeriods().stream())
            .toList();
    assertThat(updatedMonitoringPeriods).isEqualTo(List.of(expectedMonitoringPeriod));
  }

  private static Methodology buildMethodology(int id) {
    var methodology = new Methodology();
    methodology.setId(id);
    return methodology;
  }

  private static ProjectCreditingPeriodMethodology buildProjectCreditingPeriodMethodology(
      int methodologyId, double version) {
    var methodology = buildMethodology(methodologyId);
    return ProjectCreditingPeriodMethodology.builder()
        .methodology(methodology)
        .version(version)
        .build();
  }

  private static RequestMethodology buildRequestMethodology(
      Request request, int methodologyId, double version) {
    var methodology = buildMethodology(methodologyId);
    return RequestMethodology.builder()
        .request(request)
        .methodology(methodology)
        .methodologyVersion(version)
        .build();
  }
}
