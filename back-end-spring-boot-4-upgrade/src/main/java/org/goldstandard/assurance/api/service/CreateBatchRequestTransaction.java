package org.goldstandard.assurance.api.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.repository.BatchRepository;
import org.goldstandard.assurance.api.repository.BatchRequestRepository;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateBatchRequestTransaction {

  private final BatchRequestRepository batchRequestRepository;
  private final BatchRepository batchRepository;
  private final CreateRequestTransaction createRequestTransaction;
  private final AuditService auditService;

  @Transactional
  public Batch performCreate(Batch batch, List<BatchRequest> batchRequests) {
    Batch savedBatch = saveBatch(batch);
    saveBatchRequests(savedBatch, batchRequests);
    auditService.auditEvent(
        AuditEventSubject.BATCH_CREATED, createBatchAuditInformation(savedBatch));
    return savedBatch;
  }

  private Batch saveBatch(Batch batch) {
    var parentRequest = createRequestTransaction.performCreate(batch.getParentRequest());
    batch.setParentRequest(parentRequest);

    var savedBatch = batchRepository.save(batch);
    var type = savedBatch.getBatchType();
    log.info(
        "saved new batch with id {} with batch type id {} for parent request {}",
        savedBatch.getId(),
        type.getId(),
        parentRequest.getId());
    return savedBatch;
  }

  private void saveBatchRequests(Batch batch, List<BatchRequest> batchRequests) {
    batchRequests.forEach(batchRequest -> saveBatchRequest(batch, batchRequest));
  }

  private void saveBatchRequest(Batch batch, BatchRequest batchRequest) {
    var request = batchRequest.getRequest();
    request.setBatch(batch);
    var savedRequest = createRequestTransaction.performCreate(request);

    batchRequest.setBatch(batch);
    batchRequest.setRequest(savedRequest);
    BatchRequest savedBatchRequest = batchRequestRepository.save(batchRequest);
    var parentRequest = batch.getParentRequest();
    log.info(
        "saved batch request with id {} to batch with id {} and parent request with id {}",
        savedBatchRequest.getId(),
        batch.getId(),
        parentRequest.getId());
  }

  private static Map<String, Object> createBatchAuditInformation(Batch batch) {
    return Map.ofEntries(
        Map.entry("parentRequestId", batch.getParentRequest().getId()),
        Map.entry("batchId", batch.getId()));
  }
}
