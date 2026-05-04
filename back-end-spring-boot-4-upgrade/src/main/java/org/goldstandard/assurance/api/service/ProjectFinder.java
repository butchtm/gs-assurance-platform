package org.goldstandard.assurance.api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.IsDirectChildProjectPredicateFactory;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.entity.ProjectTree;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.view.ProjectTreeNodeView;
import org.goldstandard.assurance.api.entity.view.SearchableProjectView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.ProjectTreeNodeViewRepository;
import org.goldstandard.assurance.api.repository.SearchableProjectViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectFinder {

  private final ProjectRepository projectRepository;
  private final SearchableProjectViewRepository searchableProjectViewRepository;
  private final ProjectSearchSpecFactory searchSpecFactory;
  private final IsDirectChildProjectPredicateFactory isDirectChildPredicateFactory;
  private final ProjectTreeFactory projectTreeFactory;
  private final ProjectTreeNodeViewRepository projectTreeNodeViewRepository;

  public List<Project> getAllChildProjects(Project parentProject, BatchType batchType) {
    var accountProjects = projectRepository.findAllByAccountId(parentProject.getAccountId());
    var isChildPredicate = isDirectChildPredicateFactory.build(parentProject, batchType);
    return accountProjects.stream().filter(isChildPredicate).toList();
  }

  public Project forceGetProjectById(Integer id) {
    return projectRepository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Project with id %s not found", id)));
  }

  public Project forceGetProjectByGsId(String gsid) {
    return projectRepository
        .findByGsid(gsid)
        .orElseThrow(
            () -> new NotFoundException(String.format("Project with gsid %s not found", gsid)));
  }

  public Page<Project> getProjects(Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    var result = searchProjects(pageable, searchCriteria);
    var projectIds = toProjectIds(result);
    var projects =
        projectRepository.findAllByIdIn(projectIds, Project.class).stream()
            .sorted(toProjectComparator(projectIds))
            .toList();
    return new PageImpl<>(projects, pageable, result.getTotalElements());
  }

  public Page<ProjectTree> getProjectTrees(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    var rootProjects = findRootProjects(pageable, searchCriteria);
    return rootProjects.map(projectTreeFactory::buildProjectTree);
  }

  private Page<ProjectTreeNodeView> findRootProjects(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    var result = searchProjects(searchCriteria);
    var rootProjectIds = toUniqueRootProjectIds(result);
    var spec = searchSpecFactory.idIn(rootProjectIds);
    var start = Instant.now();
    try {
      return projectTreeNodeViewRepository.findAll(spec, pageable);
    } finally {
      log.info("find root projects took {}ms", Duration.between(start, Instant.now()).toMillis());
    }
  }

  public Page<ProjectMinimal> getProjectsMinimal(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    var result = searchProjects(pageable, searchCriteria);
    var projectIds = toProjectIds(result);
    var projects =
        projectRepository.findAllByIdIn(projectIds, ProjectMinimal.class).stream()
            .sorted(toProjectMinimalComparator(projectIds))
            .toList();
    return new PageImpl<>(projects, pageable, result.getTotalElements());
  }

  public List<Project> getByIds(Set<Integer> ids) {
    return IterableUtils.toList(projectRepository.findAllById(ids));
  }

  public Collection<Integer> getProjectIdsByAccountId(Integer accountId) {
    return projectRepository.findAllByAccountId(accountId).stream().map(Project::getId).toList();
  }

  private Comparator<Project> toProjectComparator(List<Integer> ids) {
    return Comparator.comparingInt(o -> ids.indexOf(o.getId()));
  }

  private Comparator<ProjectMinimal> toProjectMinimalComparator(List<Integer> ids) {
    return Comparator.comparingInt(o -> ids.indexOf(o.getId()));
  }

  private List<Integer> toProjectIds(Page<SearchableProjectView> projects) {
    return projects.map(SearchableProjectView::getId).toList();
  }

  private Set<Integer> toUniqueRootProjectIds(Collection<SearchableProjectView> projects) {
    return projects.stream()
        .map(SearchableProjectView::getRootProjectId)
        .collect(Collectors.toSet());
  }

  private Page<SearchableProjectView> searchProjects(
      Pageable pageable, ProjectSearchCriteriaDTO searchCriteria) {
    var start = Instant.now();
    try {
      var spec = searchSpecFactory.build(searchCriteria);
      return searchableProjectViewRepository.findAll(spec, pageable);
    } finally {
      log.info("search page projects took {}ms", Duration.between(start, Instant.now()).toMillis());
    }
  }

  private Collection<SearchableProjectView> searchProjects(
      ProjectSearchCriteriaDTO searchCriteria) {
    var start = Instant.now();
    try {
      var spec = searchSpecFactory.build(searchCriteria);
      return searchableProjectViewRepository.findAll(spec);
    } finally {
      log.info("search projects took {}ms", Duration.between(start, Instant.now()).toMillis());
    }
  }
}
