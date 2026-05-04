package org.goldstandard.assurance.api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ProjectTree;
import org.goldstandard.assurance.api.entity.ProjectTreeNode;
import org.goldstandard.assurance.api.entity.ProjectTreeNode.ProjectTreeNodeBuilder;
import org.goldstandard.assurance.api.entity.view.ProjectTreeNodeView;
import org.goldstandard.assurance.api.repository.ProjectTreeNodeViewRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectTreeFactory {

  private final ProjectTreeNodeViewRepository repository;

  public ProjectTree buildProjectTree(Integer projectId) {
    var project = repository.forceGetById(projectId);
    return buildProjectTree(project);
  }

  public ProjectTree buildProjectTree(ProjectTreeNodeView project) {
    var start = Instant.now();
    try {
      var masterProject = toMasterProject(project);
      if (Objects.isNull(masterProject)) {
        log.debug(
            "project {} does not have master project, cannot build project tree",
            project.getGsid());
        return new ProjectTree(toProjectTreeNode(project));
      }
      var childProjects = repository.findAllByMasterProjectId(masterProject.getId());
      log.debug(
          "building tree from {} child projects for project {} with master project {}",
          childProjects.size(),
          project.getGsid(),
          masterProject.getGsid());
      return buildTree(masterProject, childProjects);
    } finally {
      log.info(
          "build project tree for project {} {} took {}ms",
          project.getId(),
          project.getGsid(),
          Duration.between(start, Instant.now()).toMillis());
    }
  }

  private static ProjectTreeNodeView toMasterProject(ProjectTreeNodeView project) {
    if (project.isPoa()) {
      return project;
    }
    return project.getMasterProject();
  }

  private static ProjectTree buildTree(
      ProjectTreeNodeView masterProject, Collection<ProjectTreeNodeView> childProjects) {
    var regularCaseProjects =
        childProjects.stream().filter(ProjectTreeNodeView::isRegularCaseVpa).toList();
    log.debug("found {} regular case projects in tree", regularCaseProjects.size());
    var realCaseNodes =
        childProjects.stream()
            .filter(project -> project.isRealCaseVpa() || project.isLegacyVpaCpa())
            .map(realCaseProject -> toProjectTreeNode(realCaseProject, regularCaseProjects))
            .toList();
    log.debug("found {} real case projects in tree", regularCaseProjects.size());
    var masterNode = toProjectTreeNodeBuilder(masterProject).children(realCaseNodes).build();
    return new ProjectTree(masterNode);
  }

  private static ProjectTreeNode toProjectTreeNode(
      ProjectTreeNodeView parent, Collection<ProjectTreeNodeView> candidateChildProjects) {
    var children =
        candidateChildProjects.stream()
            .filter(childProject -> childProject.hasParentProjectId(parent.getId()))
            .map(ProjectTreeFactory::toProjectTreeNode)
            .toList();
    return toProjectTreeNodeBuilder(parent).children(children).build();
  }

  private static ProjectTreeNode toProjectTreeNode(ProjectTreeNodeView project) {
    return toProjectTreeNodeBuilder(project).build();
  }

  private static ProjectTreeNodeBuilder toProjectTreeNodeBuilder(ProjectTreeNodeView project) {
    return ProjectTreeNode.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .name(project.getName())
        .accountId(project.getAccountId())
        .classificationId(project.getProjectClassification().getId())
        .bundle(project.getBundle())
        .status(project.getProjectStatus())
        .locations(project.getProjectLocations())
        .types(project.getProjectTypes())
        .children(Collections.emptyList());
  }
}
