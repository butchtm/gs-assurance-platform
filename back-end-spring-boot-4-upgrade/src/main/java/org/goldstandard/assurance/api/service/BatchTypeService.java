package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.entity.BatchMapping;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.repository.BatchMappingRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BatchTypeService {
  private final BatchMappingRepository repository;
  private final ProjectFinder projectFinder;

  public List<Project> getBatchCandidateProjects(GetBatchCandidateProjectsDTO dto) {
    var project = projectFinder.forceGetProjectById(dto.getProjectId());
    var batchMapping =
        getAndValidateBatchTypeMapping(project, dto.getRequestTypeId(), dto.getBatchTypeId());
    return projectFinder.getAllChildProjects(project, batchMapping.getBatchType());
  }

  public List<BatchType> getBatchTypes(GetBatchTypesDTO dto) {
    var project = projectFinder.forceGetProjectById(dto.getProjectId());
    return getBatchTypeMappings(project, dto.getRequestTypeId()).stream()
        .map(BatchMapping::getBatchType)
        .toList();
  }

  private BatchMapping getAndValidateBatchTypeMapping(
      Project project, Integer requestTypeId, Integer batchTypeId) {
    return getBatchTypeMappings(project, requestTypeId).stream()
        .filter(m -> m.getBatchType().getId().equals(batchTypeId))
        .findFirst()
        .orElseThrow(
            () ->
                new BatchRequestException(
                    "Batch type id %d not allowed for project %d and request type id %d",
                    batchTypeId, project.getId(), requestTypeId));
  }

  private List<BatchMapping> getBatchTypeMappings(Project project, Integer requestTypeId) {
    var classification = project.getProjectClassification();
    return repository.findAllByRequestTypeIdAndProjectClassificationId(
        requestTypeId, classification.getId());
  }
}
