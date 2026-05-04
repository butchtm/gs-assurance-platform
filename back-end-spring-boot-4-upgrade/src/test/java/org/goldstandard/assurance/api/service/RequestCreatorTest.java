package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.RequestMethodologyId;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestCreatorTest {

  static final Integer PROJECT_ID = 123;

  @InjectMocks RequestCreator creator;

  @Mock RequestFactory factory;

  @Mock CreateRequestTransaction transaction;

  @Captor ArgumentCaptor<Request> requestArgumentCaptor;

  @Test
  void createRequest() {
    var create = RequestCreateUpdateDTOMother.build();
    var request = createRequestObject();

    when(factory.buildRequest(PROJECT_ID, create)).thenReturn(request);
    when(transaction.performCreate(request)).thenReturn(request);

    creator.create(PROJECT_ID, create);

    verify(transaction).performCreate(requestArgumentCaptor.capture());
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
  }

  @Test
  void createRequest_withMethodologies() {
    var create = RequestCreateUpdateDTOMother.build();
    var request = createRequestObjectWithMethodologies();

    when(factory.buildRequest(PROJECT_ID, create)).thenReturn(request);
    when(transaction.performCreate(request)).thenReturn(request);

    creator.create(PROJECT_ID, create);

    verify(transaction).performCreate(requestArgumentCaptor.capture());
    var capturedRequest = requestArgumentCaptor.getValue();
    assertThat(capturedRequest).isEqualTo(request);
    assertThat(capturedRequest.getRequestMethodologies()).hasSize(2);
  }

  private static Request createRequestObjectWithMethodologies() {
    var request = createRequestObject();

    var methodology1 =
        RequestMethodology.builder()
            .id(new RequestMethodologyId(1, 1001))
            .methodologyVersion(1.0)
            .build();

    var methodology2 =
        RequestMethodology.builder()
            .id(new RequestMethodologyId(1, 1002))
            .methodologyVersion(2.0)
            .build();

    request.setRequestMethodologies(List.of(methodology1, methodology2));

    return request;
  }

  private static Request createRequestObject() {
    var request = new Request();
    request.setProject(mock(Project.class));
    request.setRequestName("Review No. 1");
    request.setRequestType(new RequestType(1));
    request.setIndex(0);
    request.setSameRequestTypeCount(0);
    request.setEmissionReductionAmount(123);
    request.setCreditingPeriod(1);
    request.setStartDate(ZonedDateTime.now());
    request.setCurrentRequestStatus(RequestStatus.inPreparation());
    request.setRequestDocumentStatus(0);
    request.setUpdatedByUserId(12345);
    request.setManuallyAdded(true);
    return request;
  }
}
