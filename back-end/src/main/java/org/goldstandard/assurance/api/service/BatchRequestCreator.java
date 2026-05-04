package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestCreator {

  private final BatchRequestCreateValidator validator;
  private final CreateBatchRequestTransaction transaction;
  private final BatchRequestFactory batchRequestFactory;
  private final RequestAccessPolicy accessPolicy;

  public Integer createBatchRequest(BatchRequestCreateDTO create) {
    validator.validate(create);
    List<Request> requests = batchRequestFactory.buildRequests(create);
    Request parentRequest = requests.getFirst();
    accessPolicy.validateCanCreateRequest(parentRequest.getProject());
    Batch batch = batchRequestFactory.buildBatch(create.getBatchTypeId(), parentRequest);
    List<BatchRequest> batchRequests = batchRequestFactory.toBatchRequests(batch, requests);
    Batch savedBatch = transaction.performCreate(batch, batchRequests);
    return savedBatch.getId();
  }
}
