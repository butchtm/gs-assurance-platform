package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestServiceTest {

  @InjectMocks BatchRequestService requestService;

  @Mock BatchRequestCreator creator;

  @Mock BatchRequestFinder finder;

  @Mock BatchTypeService typeService;

  @Test
  void shouldReturnBatchTypes() {
    GetBatchTypesDTO dto = new GetBatchTypesDTO(1, 2);
    var expectedBatchTypes = List.of(new BatchType());
    when(typeService.getBatchTypes(dto)).thenReturn(expectedBatchTypes);

    var batchTypes = requestService.getBatchTypes(dto);

    assertThat(batchTypes).isEqualTo(expectedBatchTypes);
  }

  @Test
  void shouldReturnCandidateProjects() {
    var dto =
        GetBatchCandidateProjectsDTO.builder().requestTypeId(3).batchTypeId(4).projectId(5).build();
    var expectedCandidateProjects = List.of(new Project());
    when(typeService.getBatchCandidateProjects(dto)).thenReturn(expectedCandidateProjects);

    var candidateProjects = requestService.getCandidateProjects(dto);

    assertThat(candidateProjects).isEqualTo(expectedCandidateProjects);
  }

  @Test
  void shouldCreateBatchRequest() {
    BatchRequestCreateDTO dto =
        BatchRequestCreateDTO.builder()
            .requestTypeId(6)
            .batchTypeId(7)
            .projectRequestInfos(List.of())
            .build();
    var expectedBatchId = 8;
    when(creator.createBatchRequest(dto)).thenReturn(expectedBatchId);

    var batchId = requestService.createBatchRequest(dto);

    assertThat(batchId).isEqualTo(expectedBatchId);
  }

  @Test
  void shouldReturnRequestsInBatch() {
    int batchId = 9;
    var expectedBatchRequests = mock(BatchRequests.class);
    when(finder.getRequestsInBatch(batchId)).thenReturn(expectedBatchRequests);

    var batchRequests = requestService.getRequestsInBatch(batchId);

    assertThat(batchRequests).isEqualTo(expectedBatchRequests);
  }
}
