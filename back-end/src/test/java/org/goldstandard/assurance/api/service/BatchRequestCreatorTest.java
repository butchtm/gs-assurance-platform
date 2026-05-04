package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestCreatorTest {

  @InjectMocks BatchRequestCreator creator;

  @Mock BatchRequestCreateValidator validator;

  @Mock CreateBatchRequestTransaction transaction;

  @Mock BatchRequestFactory batchRequestFactory;

  @Mock RequestAccessPolicy accessPolicy;

  @Test
  void shouldThrowExceptionIfValidationFails() {
    var dto = mock(BatchRequestCreateDTO.class);
    var expectedError = new BatchRequestException("batch-validation-error");
    doThrow(expectedError).when(validator).validate(dto);

    Throwable error = catchThrowable(() -> creator.createBatchRequest(dto));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionUserNotPermittedToAccessParentProject() {
    var dto = mock(BatchRequestCreateDTO.class);
    var parentRequest = buildRequestForProject(100, 1);
    var childRequest = buildRequestForProject(200, 2);
    when(batchRequestFactory.buildRequests(dto)).thenReturn(List.of(parentRequest, childRequest));
    var expectedError = new PermissionException("permission-error");
    doThrow(expectedError).when(accessPolicy).validateCanCreateRequest(parentRequest.getProject());

    Throwable error = catchThrowable(() -> creator.createBatchRequest(dto));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldSaveBatchRequest() {
    var dto = mock(BatchRequestCreateDTO.class);
    var parentRequest = buildRequestForProject(100, 1);
    var childRequest = buildRequestForProject(200, 2);
    var requests = List.of(parentRequest, childRequest);
    when(batchRequestFactory.buildRequests(dto)).thenReturn(requests);
    var batch = givenBatchCreated(dto, parentRequest);
    var batchRequests = givenBatchRequestsCreated(batch, requests);
    var savedBatch = givenSavedBatchWithId(batch, batchRequests);

    int batchId = creator.createBatchRequest(dto);

    assertThat(batchId).isEqualTo(savedBatch.getId());
  }

  private Batch givenBatchCreated(BatchRequestCreateDTO dto, Request parentRequest) {
    var batch = mock(Batch.class);
    when(batchRequestFactory.buildBatch(dto.getBatchTypeId(), parentRequest)).thenReturn(batch);
    return batch;
  }

  private List<BatchRequest> givenBatchRequestsCreated(Batch batch, List<Request> requests) {
    var batchRequests = List.of(mock(BatchRequest.class), mock(BatchRequest.class));
    when(batchRequestFactory.toBatchRequests(batch, requests)).thenReturn(batchRequests);
    return batchRequests;
  }

  private Batch givenSavedBatchWithId(Batch batch, List<BatchRequest> batchRequests) {
    var savedBatch = new Batch();
    savedBatch.setId(300);
    when(transaction.performCreate(batch, batchRequests)).thenReturn(savedBatch);
    return savedBatch;
  }

  private static Request buildRequestForProject(int requestId, int projectId) {
    return Request.builder().id(requestId).project(new Project(projectId)).build();
  }
}
