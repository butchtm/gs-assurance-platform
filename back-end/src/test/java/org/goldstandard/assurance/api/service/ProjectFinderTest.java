package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.DefaultIsDirectChildProjectPredicate;
import org.goldstandard.assurance.api.entity.IsDirectChildProjectPredicateFactory;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.view.SearchableProjectView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.SearchableProjectViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProjectFinderTest {

  @InjectMocks ProjectFinder finder;

  @Mock ProjectRepository projectRepository;

  @Mock SearchableProjectViewRepository searchableProjectViewRepository;

  @Mock ProjectSearchSpecFactory searchSpecFactory;

  @Mock IsDirectChildProjectPredicateFactory isDirectChildPredicateFactory;

  @Mock Specification<SearchableProjectView> spec;

  @Test
  void getAllDirectChildAccountProjects() {
    var accountId = 99;
    var parent = Project.builder().id(1).accountId(accountId).build();
    var child1 = Project.builder().id(2).accountId(accountId).build();
    var child2 = Project.builder().id(3).accountId(accountId).build();
    when(projectRepository.findAllByAccountId(parent.getAccountId()))
        .thenReturn(List.of(child1, child2));
    var isDirectChildPredicate = mock(DefaultIsDirectChildProjectPredicate.class);
    var batchType = mock(BatchType.class);
    when(isDirectChildPredicateFactory.build(parent, batchType)).thenReturn(isDirectChildPredicate);
    when(isDirectChildPredicate.test(child1)).thenReturn(false);
    when(isDirectChildPredicate.test(child2)).thenReturn(true);

    var childProjects = finder.getAllChildProjects(parent, batchType);

    assertThat(childProjects).containsExactly(child2);
  }

  @Test
  void getProjects() {
    Pageable pageable = Pageable.ofSize(1);
    ProjectSearchCriteriaDTO criteria = ProjectSearchCriteriaDTO.builder().build();
    when(searchSpecFactory.build(criteria)).thenReturn(spec);
    var projectIds = List.of(1, 2);
    var views = givenSearchableViewsWithIds(projectIds);
    Page<SearchableProjectView> searchPage = new PageImpl<>(views, pageable, views.size());
    when(searchableProjectViewRepository.findAll(spec, pageable)).thenReturn(searchPage);
    var expectedProjects = List.of(mock(Project.class), mock(Project.class));
    when(projectRepository.findAllByIdIn(projectIds, Project.class)).thenReturn(expectedProjects);

    var page = finder.getProjects(pageable, criteria);

    assertThat(page.getTotalPages()).isEqualTo(expectedProjects.size());
    assertThat(page.getTotalElements()).isEqualTo(expectedProjects.size());
    assertThat(page.getContent()).containsExactlyElementsOf(expectedProjects);
  }

  @Test
  void shouldReturnProjectByGsId() {
    var gsId = "GS10001";
    var project = mock(Project.class);
    when(projectRepository.findByGsid(gsId)).thenReturn(Optional.of(project));

    var result = finder.forceGetProjectByGsId(gsId);

    assertThat(result).isEqualTo(project);
  }

  @Test
  void shouldThrowExceptionIfProjectNotFoundByGsId() {
    var gsId = "GS10002";
    when(projectRepository.findByGsid(gsId)).thenReturn(Optional.empty());

    var error = catchThrowable(() -> finder.forceGetProjectByGsId(gsId));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Project with gsid %s not found", gsId);
  }

  @Test
  void shouldReturnProjectById() {
    var id = 1;
    var project = mock(Project.class);
    when(projectRepository.findById(id)).thenReturn(Optional.of(project));

    var result = finder.forceGetProjectById(id);

    assertThat(result).isEqualTo(project);
  }

  @Test
  void shouldThrowExceptionIfProjectNotFoundById() {
    var id = 2;
    when(projectRepository.findById(id)).thenReturn(Optional.empty());

    var error = catchThrowable(() -> finder.forceGetProjectById(id));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Project with id %d not found", id);
  }

  private List<SearchableProjectView> givenSearchableViewsWithIds(Collection<Integer> ids) {
    return ids.stream().map(this::givenSearchableViewWithId).toList();
  }

  private SearchableProjectView givenSearchableViewWithId(int id) {
    var view = mock(SearchableProjectView.class);
    when(view.getId()).thenReturn(id);
    return view;
  }
}
