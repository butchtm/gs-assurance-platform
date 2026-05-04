package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BatchRequestService {

  private final BatchRequestCreator creator;
  private final BatchTypeService batchTypeService;
  private final BatchRequestFinder finder;

  public List<BatchType> getBatchTypes(GetBatchTypesDTO dto) {
    return batchTypeService.getBatchTypes(dto);
  }

  public List<Project> getCandidateProjects(GetBatchCandidateProjectsDTO dto) {
    return batchTypeService.getBatchCandidateProjects(dto);
  }

  public Integer createBatchRequest(BatchRequestCreateDTO create) {
    return creator.createBatchRequest(create);
  }

  public BatchRequests getRequestsInBatch(int batchId) {
    return finder.getRequestsInBatch(batchId);
  }
}
