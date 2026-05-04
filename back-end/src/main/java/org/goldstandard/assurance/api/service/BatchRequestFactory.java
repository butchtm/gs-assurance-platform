package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestFactory {

  private final RequestFactory requestFactory;

  public List<Request> buildRequests(BatchRequestCreateDTO create) {
    return create.getProjectRequestInfos().stream()
        .map(project -> requestFactory.buildRequestForBatch(create, project))
        .toList();
  }

  public Batch buildBatch(int batchTypeId, Request parentRequest) {
    return Batch.builder()
        .batchType(new BatchType(batchTypeId))
        .parentRequest(parentRequest)
        .build();
  }

  public List<BatchRequest> toBatchRequests(Batch batch, List<Request> requests) {
    return requests.stream().map(r -> buildBatchRequest(r, batch)).toList();
  }

  private static BatchRequest buildBatchRequest(Request request, Batch batch) {
    return BatchRequest.builder()
        .request(request)
        .project(request.getProject())
        .batch(batch)
        .build();
  }
}
