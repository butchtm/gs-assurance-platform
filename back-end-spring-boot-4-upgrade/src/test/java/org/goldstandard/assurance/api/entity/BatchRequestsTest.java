package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class BatchRequestsTest {

  @Test
  void shouldReturnParent() {
    BatchRequests batchRequests = buildBatchRequests();

    var parent = batchRequests.getParent();

    assertThat(parent.getId()).isEqualTo(8);
  }

  @Test
  void shouldReturnChildren() {
    BatchRequests batchRequests = buildBatchRequests();

    var children = batchRequests.getChildren();

    assertThat(children).map(BatchRequest::getId).containsExactly(7, 9);
  }

  private static BatchRequests buildBatchRequests() {
    var parentRequest = buildRequest(88);
    var request1 = toBatchRequest(7, buildRequest(77));
    var request2 = toBatchRequest(8, parentRequest);
    var request3 = toBatchRequest(9, buildRequest(99));
    return BatchRequests.builder()
        .batch(buildBatch(parentRequest))
        .requests(List.of(request1, request2, request3))
        .build();
  }

  private static Batch buildBatch(Request parentRequest) {
    var batch = new Batch();
    batch.setParentRequest(parentRequest);
    return batch;
  }

  private static Request buildRequest(int id) {
    var request = new Request();
    request.setId(id);
    return request;
  }

  private static BatchRequest toBatchRequest(int id, Request request) {
    var batchRequest = new BatchRequest();
    batchRequest.setId(id);
    batchRequest.setRequest(request);
    batchRequest.setProject(request.getProject());
    return batchRequest;
  }
}
