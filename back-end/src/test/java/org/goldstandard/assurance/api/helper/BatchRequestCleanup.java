package org.goldstandard.assurance.api.helper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.BatchRepository;
import org.goldstandard.assurance.api.repository.BatchRequestRepository;
import org.goldstandard.assurance.api.repository.MethodologyUpdateRepository;
import org.goldstandard.assurance.api.repository.ProjectMethodologyUpdateRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestCleanup {

  private final BatchRequestRepository batchRequestRepository;
  private final BatchRepository batchRepository;
  private final BatchRequestFinder finder;
  private final RequestRepository requestRepository;
  private final RequestRoundRepository roundRepository;
  private final RequestMethodologyRepository requestMethodologyRepository;
  private final StakeholderConsultationRepository stakeholderConsultationRepository;
  private final ProjectMethodologyUpdateRepository projectMethodologyUpdateRepository;
  private final MethodologyUpdateRepository methodologyUpdateRepository;

  @Transactional
  public void deleteBatchRequests(BatchRequests batchRequests) {
    deleteBatchRequests(batchRequests.getBatch().getId());
  }

  @Transactional
  public void deleteBatchRequests(Integer batchId) {
    var batchRequests = finder.getRequestsInBatch(batchId);
    batchRequests.getRequests().forEach(this::removeForeignConstraints);
    batchRepository.deleteById(batchId);
  }

  private void removeForeignConstraints(BatchRequest batchRequest) {
    Request request = batchRequest.getRequest();

    removeForeignConstraints(batchRequest.getBatch());
    removeForeignConstraints(request);

    batchRequestRepository.delete(batchRequest);
    roundRepository.deleteAll(request.getRequestRounds());
    requestMethodologyRepository
        .getAllByRequestId(request.getId())
        .forEach(requestMethodologyRepository::delete);
    stakeholderConsultationRepository
        .getAllByRequestId(request.getId())
        .forEach(stakeholderConsultationRepository::delete);
    deleteLinkedMethodologyUpdates(request);
    requestRepository.deleteById(request.getId());
  }

  private void deleteLinkedMethodologyUpdates(Request request) {
    var projectMethodologyUpdates = projectMethodologyUpdateRepository.getAllByRequest(request);
    for (var projectMethodologyUpdate : projectMethodologyUpdates) {
      projectMethodologyUpdate.getMethodologyUpdates().forEach(methodologyUpdateRepository::delete);
      projectMethodologyUpdateRepository.delete(projectMethodologyUpdate);
    }
  }

  private void removeForeignConstraints(Batch batch) {
    batch.setParentRequest(null);
    batchRepository.save(batch);
  }

  private void removeForeignConstraints(Request request) {
    request.setBatch(null);
    requestRepository.save(request);
  }
}
