package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectTransactionChildUpdaterTest {

  @InjectMocks ProjectTransactionChildUpdater childUpdater;

  @Mock ProjectRepository projectRepository;

  @Mock Project originalProject;

  @Mock Project updatedProject;

  @Mock Project masterProject;

  @Captor ArgumentCaptor<List<Project>> captor;

  @Test
  void shouldDoNothingIfOriginalProjectIsNotRealCase() {
    when(originalProject.isRealCaseVpa()).thenReturn(false);

    var updatedProjects = childUpdater.updateChildrenIfRequired(originalProject, updatedProject);

    assertThat(updatedProjects).isEmpty();
    verifyNoInteractions(projectRepository);
  }

  @Test
  void shouldDoNothingIfUpdatedProjectIsNotRealCase() {
    when(originalProject.isRealCaseVpa()).thenReturn(true);
    when(updatedProject.isRealCaseVpa()).thenReturn(false);

    var updatedProjects = childUpdater.updateChildrenIfRequired(originalProject, updatedProject);

    assertThat(updatedProjects).isEmpty();
    verifyNoInteractions(projectRepository);
  }

  @Test
  void shouldDoNothingIfMasterProjectNotChangedForRealCase() {
    when(originalProject.isRealCaseVpa()).thenReturn(true);
    when(updatedProject.isRealCaseVpa()).thenReturn(true);
    when(updatedProject.hasSameMasterAs(originalProject)).thenReturn(true);

    var updatedProjects = childUpdater.updateChildrenIfRequired(originalProject, updatedProject);

    assertThat(updatedProjects).isEmpty();
    verifyNoInteractions(projectRepository);
  }

  @Test
  void shouldUpdateMasterProjectForAllChildProjectsIfMasterProjectChangedForRealCase() {
    when(originalProject.isRealCaseVpa()).thenReturn(true);
    when(updatedProject.isRealCaseVpa()).thenReturn(true);
    when(updatedProject.hasSameMasterAs(originalProject)).thenReturn(false);
    when(updatedProject.getMasterProject()).thenReturn(masterProject);
    var childProjects = givenChildProjects(updatedProject.getId());
    var savedProjects = givenSavedProjects(childProjects);

    var updatedProjects = childUpdater.updateChildrenIfRequired(originalProject, updatedProject);

    verify(projectRepository).saveAll(captor.capture());
    assertThat(captor.getValue()).containsExactlyElementsOf(childProjects);
    assertThat(captor.getValue())
        .map(Project::getMasterProject)
        .containsExactly(masterProject, masterProject);
    assertThat(updatedProjects).isEqualTo(savedProjects);
  }

  private Collection<Project> givenChildProjects(Integer projectId) {
    var childProjects = buildChildProjects();
    when(projectRepository.findAllByParentProjectId(projectId)).thenReturn(childProjects);
    return childProjects;
  }

  private Collection<Project> givenSavedProjects(Collection<Project> projects) {
    var savedProjects = List.of(mock(Project.class), mock(Project.class));
    when(projectRepository.saveAll(projects)).thenReturn(savedProjects);
    return savedProjects;
  }

  private static Collection<Project> buildChildProjects() {
    var childProject1 = new Project();
    var childProject2 = new Project();
    return List.of(childProject1, childProject2);
  }
}
