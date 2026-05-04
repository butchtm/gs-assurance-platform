package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.entity.BatchMapping;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.repository.BatchMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchTypeServiceTest {

  private static final int PROJECT_ID = 1;
  private static final int REQUEST_TYPE_ID = 2;
  private static final BatchType BATCH_TYPE = new BatchType(3);

  @InjectMocks BatchTypeService batchTypeService;

  @Mock ProjectFinder projectFinder;

  @Mock BatchMappingRepository repository;

  @Test
  void shouldNotThrowExceptionIfRequestBatchTypeIsConfiguredForProjectAndRequestType() {
    var dto = new GetBatchCandidateProjectsDTO(REQUEST_TYPE_ID, BATCH_TYPE.getId(), PROJECT_ID);
    var mapping = buildBatchMapping();
    var project = givenProject();
    givenBatchTypeMappings(project, dto.getRequestTypeId(), mapping);
    when(projectFinder.getAllChildProjects(project, BATCH_TYPE))
        .thenReturn(List.of(mock(Project.class)));

    ThrowingCallable call = () -> batchTypeService.getBatchCandidateProjects(dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfRequestBatchTypeNotConfiguredForProjectAndRequestType() {
    var dto = new GetBatchCandidateProjectsDTO(REQUEST_TYPE_ID, 999, PROJECT_ID);
    var mapping = buildBatchMapping();
    var project = givenProject();
    givenBatchTypeMappings(project, dto.getRequestTypeId(), mapping);

    var error = catchThrowable(() -> batchTypeService.getBatchCandidateProjects(dto));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("Batch type id 999 not allowed for project 1 and request type id 2");
  }

  @Test
  void shouldReturnBatchTypesForProjectAndRequestType() {
    var dto = new GetBatchTypesDTO(REQUEST_TYPE_ID, PROJECT_ID);
    var mapping = buildBatchMapping();
    var project = givenProject();
    givenBatchTypeMappings(project, dto.getRequestTypeId(), mapping);

    var batchTypes = batchTypeService.getBatchTypes(dto);

    assertThat(batchTypes).containsExactly(BATCH_TYPE);
  }

  private Project givenProject() {
    var project =
        Project.builder()
            .id(PROJECT_ID)
            .projectClassification(ProjectClassification.realCaseVpa())
            .build();
    when(projectFinder.forceGetProjectById(project.getId())).thenReturn(project);
    return project;
  }

  private void givenBatchTypeMappings(
      Project project, Integer requestTypeId, BatchMapping mapping) {
    var classification = project.getProjectClassification();
    when(repository.findAllByRequestTypeIdAndProjectClassificationId(
            requestTypeId, classification.getId()))
        .thenReturn(List.of(mapping));
  }

  private static BatchMapping buildBatchMapping() {
    var mapping = new BatchMapping();
    mapping.setBatchType(BATCH_TYPE);
    return mapping;
  }
}
