package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectUpdateFinderTest {

  @InjectMocks ProjectUpdateFinder finder;

  @Mock BatchRequestFinder batchRequestFinder;

  @Test
  void shouldConvertRequestsToProjects() {
    var parentProject = buildProject(1);
    var childProject1 = buildProject(2);
    var childProject2 = buildProject(3);
    var childProjects = List.of(toRequest(childProject1), toRequest(childProject2));

    var projects =
        finder.toProjectsAppendingLegacyBatchProjectsIfPresent(
            toRequest(parentProject), childProjects);

    assertThat(projects).containsExactly(parentProject, childProject1, childProject2);
  }

  @Test
  void shouldAppendLegacyBatchRequestProjectsIfPresent() {
    var project = buildProject(1);
    var legacyBatchProject = buildProject(4);
    var request = toRequest(project);
    when(batchRequestFinder.getLegacyBatchProjects(request.getId()))
        .thenReturn(List.of(legacyBatchProject));

    var projects = finder.toProjectsAppendingLegacyBatchProjectsIfPresent(request, List.of());

    assertThat(projects).containsExactly(project, legacyBatchProject);
  }

  @Test
  void shouldNotAppendLegacyBatchRequestProjectsIfPresentButDuplicated() {
    var project = buildProject(1);
    var legacyBatchProject = buildProject(1);
    var request = toRequest(project);
    when(batchRequestFinder.getLegacyBatchProjects(request.getId()))
        .thenReturn(List.of(legacyBatchProject));

    var projects =
        finder.toProjectsAppendingLegacyBatchProjectsIfPresent(toRequest(project), List.of());

    assertThat(projects).containsExactly(project);
  }

  private static Project buildProject(int id) {
    return Project.builder().id(id).projectStatus(buildProjectStatus(id)).build();
  }

  private static ProjectStatus buildProjectStatus(int sequence) {
    var status = new ProjectStatus(sequence);
    status.setSequence(sequence);
    return status;
  }

  private static Request toRequest(Project project) {
    return Request.builder().id(99).project(project).build();
  }
}
