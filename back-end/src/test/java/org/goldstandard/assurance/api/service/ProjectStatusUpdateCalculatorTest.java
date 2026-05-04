package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectStatusUpdateMapping;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.repository.ProjectStatusUpdateMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectStatusUpdateCalculatorTest {

  private static final RequestType REQUEST_TYPE = new RequestType(1);
  private static final RequestStatus REQUEST_STATUS = new RequestStatus(2);

  @InjectMocks ProjectStatusUpdateCalculator calculator;

  @Mock ProjectStatusUpdateMappingRepository repository;

  @Test
  void shouldNotReturnNewProjectStatusIfMappingIsNotFound() {
    var project = buildProject();
    var request = buildRequest(project);
    when(repository.findByRequestStatusIdAndRequestTypeId(
            REQUEST_STATUS.getId(), REQUEST_TYPE.getId()))
        .thenReturn(Optional.empty());

    var result = calculator.calculateNewProjectStatus(request);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnNewProjectStatusIfMappingFound() {
    var currentStatus = mock(ProjectStatus.class);
    var project = buildProject(currentStatus);
    var request = buildRequest(project);
    var newStatus = mock(ProjectStatus.class);
    givenMappingFoundWithStatus(newStatus);

    var result = calculator.calculateNewProjectStatus(request);

    assertThat(result).contains(newStatus);
  }

  private void givenMappingFoundWithStatus(ProjectStatus status) {
    var mapping = buildStatusMapping(status);
    when(repository.findByRequestStatusIdAndRequestTypeId(
            REQUEST_STATUS.getId(), REQUEST_TYPE.getId()))
        .thenReturn(Optional.of(mapping));
  }

  private static Request buildRequest(Project project) {
    return Request.builder()
        .requestType(REQUEST_TYPE)
        .currentRequestStatus(REQUEST_STATUS)
        .project(project)
        .build();
  }

  private static Project buildProject() {
    return buildProject(new ProjectStatus());
  }

  private static Project buildProject(ProjectStatus status) {
    return Project.builder().projectStatus(status).build();
  }

  private static ProjectStatusUpdateMapping buildStatusMapping(ProjectStatus status) {
    return ProjectStatusUpdateMapping.builder()
        .projectStatus(status)
        .requestType(REQUEST_TYPE)
        .requestStatus(REQUEST_STATUS)
        .build();
  }
}
