package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.goldstandard.assurance.api.dto.CreditingPeriodMethodologyDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestFactoryTest {

  public static final Integer PROJECT_ID = 123;

  @InjectMocks RequestFactory requestFactory;

  @Mock ProjectFinder projectFinder;

  @Mock RequestFinder requestFinder;

  @Mock RequestNameCalculator nameCalculator;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Mock RequestMethodologyFactory methodologyFactory;

  @Mock Clock clock;

  @Mock RequestDraftCreditingPeriodFactory draftCreditingPeriodFactory;

  @Mock RequestDraftMonitoringPeriodFactory draftMonitoringPeriodFactory;

  @Test
  void shouldBuildRequest() {
    var create = buildRequestCreateUpdate();
    var request = createRequest(0);
    var project = createProject();

    mockTimeNow();
    when(projectFinder.forceGetProjectById(PROJECT_ID)).thenReturn(project);
    when(requestFinder.getByProjectId(PROJECT_ID)).thenReturn(Collections.emptyList());
    when(nameCalculator.calculateName(create.getRequestTypeId(), 0))
        .thenReturn(request.getRequestName());
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(12345);
    var expectedMethodologies = List.of(mock(RequestMethodology.class));
    when(methodologyFactory.toMethodologies(eq(create.getMethodologies()), any(Request.class)))
        .thenReturn(expectedMethodologies);
    var expectedDraftCreditingPeriod = mock(RequestCreditingPeriod.class);
    when(draftCreditingPeriodFactory.build(create.getCreditingPeriod()))
        .thenReturn(Optional.of(expectedDraftCreditingPeriod));
    var expectedDraftMonitoringPeriod = mock(RequestMonitoringPeriod.class);
    when(draftMonitoringPeriodFactory.build(any(Request.class), eq(create.getMonitoringPeriod())))
        .thenReturn(Optional.of(expectedDraftMonitoringPeriod));

    var result = requestFactory.buildRequest(PROJECT_ID, create);

    assertThat(result.getIndex()).isEqualTo(request.getIndex());
    assertThat(result.getRequestType().getId()).isEqualTo(request.getRequestType().getId());
    assertThat(result.getStartDate().toEpochSecond())
        .isEqualTo(create.getStartDate().toEpochSecond());
    assertThat(result.getRequestMethodologies()).isEqualTo(expectedMethodologies);
    assertThat(result.isMigrated()).isFalse();
    assertThat(result.getDraftCreditingPeriod()).contains(expectedDraftCreditingPeriod);
  }

  @Test
  void shouldSetSameRequestTypeCountAndIndexCorrectly() {
    var create = buildRequestCreateUpdate();
    var existingRequests =
        IntStream.range(0, 3).mapToObj(RequestFactoryTest::createRequest).toList();
    var newRequest = createRequest(existingRequests.size());
    var project = createProject();

    mockTimeNow();
    when(projectFinder.forceGetProjectById(PROJECT_ID)).thenReturn(project);
    when(requestFinder.getByProjectId(PROJECT_ID)).thenReturn(existingRequests);
    when(nameCalculator.calculateName(create.getRequestTypeId(), existingRequests.size()))
        .thenReturn(newRequest.getRequestName());
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(12345);

    var result = requestFactory.buildRequest(PROJECT_ID, create);

    assertThat(result.getIndex()).isEqualTo(newRequest.getIndex());
    assertThat(result.getSameRequestTypeCount()).isEqualTo(existingRequests.size());
  }

  private static Request createRequest(Integer index) {
    var request = new Request();
    request.setIndex(index);
    request.setRequestName("Review no. 1");
    request.setRequestType(new RequestType(1000));
    return request;
  }

  private static Project createProject() {
    return new Project(PROJECT_ID);
  }

  private static RequestCreateUpdateDTO buildRequestCreateUpdate() {
    return RequestCreateUpdateDTOMother.builder(1).requestTypeId(1000).build();
  }

  private static List<CreditingPeriodMethodologyDTO> buildMethodologyDTOs() {
    return List.of(buildMethodologyDTO(1001, 1.0), buildMethodologyDTO(1002, 2.0));
  }

  private static CreditingPeriodMethodologyDTO buildMethodologyDTO(int id, double version) {
    return CreditingPeriodMethodologyDTO.builder().id(id).version(version).build();
  }

  private void mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }
}
