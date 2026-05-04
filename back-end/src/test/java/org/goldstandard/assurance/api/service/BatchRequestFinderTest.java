package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.repository.BatchRepository;
import org.goldstandard.assurance.api.repository.BatchRequestRepository;
import org.goldstandard.assurance.api.repository.LegacyBatchRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestFinderTest {

  @InjectMocks BatchRequestFinder batchRequestFinder;

  @Mock BatchRepository batchRepository;

  @Mock BatchRequestRepository batchRequestRepository;

  @Mock LegacyBatchRequestRepository legacyBatchRequestRepository;

  @Test
  void shouldPopulateBatchOnBatchRequests() {
    var batchId = 1;
    var expectedBatch = givenBatch(batchId);
    when(batchRepository.forceFindById(batchId)).thenReturn(expectedBatch);

    var batchRequests = batchRequestFinder.getRequestsInBatch(batchId);

    assertThat(batchRequests.getBatch()).isEqualTo(expectedBatch);
  }

  @Test
  void shouldReturnRequestsForBatchId() {
    var batchId = 2;
    var batch = givenBatch(batchId);
    when(batchRepository.forceFindById(batchId)).thenReturn(batch);
    var expectedRequests = List.of(mock(BatchRequest.class));
    when(batchRequestRepository.findAllByBatchId(batchId)).thenReturn(expectedRequests);

    var batchRequests = batchRequestFinder.getRequestsInBatch(batchId);

    assertThat(batchRequests.getRequests()).isEqualTo(expectedRequests);
  }

  @Test
  void shouldReturnLegacyBatchRequestProjectIds() {
    var requestId = 1;
    var expectedIds = List.of(2);
    when(legacyBatchRequestRepository.findAllProjectIdsInLegacyBatch(requestId))
        .thenReturn(expectedIds);

    var ids = batchRequestFinder.getLegacyBatchProjectIds(requestId);

    assertThat(ids).isEqualTo(expectedIds);
  }

  @Test
  void shouldReturnLegacyBatchRequestProjects() {
    var requestId = 1;
    var expectedProjects = List.of(new Project());
    when(legacyBatchRequestRepository.findAllProjectsInLegacyBatch(requestId))
        .thenReturn(expectedProjects);

    var projects = batchRequestFinder.getLegacyBatchProjects(requestId);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  private static Batch givenBatch(int id) {
    var batch = new Batch();
    batch.setId(id);
    return batch;
  }
}
