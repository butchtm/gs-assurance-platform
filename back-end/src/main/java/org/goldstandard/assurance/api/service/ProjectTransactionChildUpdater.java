package org.goldstandard.assurance.api.service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTransactionChildUpdater {

  private final ProjectRepository projectRepository;

  @Transactional
  public Collection<Project> updateChildrenIfRequired(Project original, Project updated) {
    if (hasRealCaseVpaMasterChanged(original, updated)) {
      return updateMasterForAllChildren(updated);
    }
    return Collections.emptyList();
  }

  private boolean hasRealCaseVpaMasterChanged(Project original, Project updated) {
    return original.isRealCaseVpa()
        && updated.isRealCaseVpa()
        && !updated.hasSameMasterAs(original);
  }

  private Collection<Project> updateMasterForAllChildren(Project parent) {
    var children = findChildren(parent);
    return ensureConsistentMaster(parent, children);
  }

  private Collection<Project> findChildren(Project parent) {
    return projectRepository.findAllByParentProjectId(parent.getId());
  }

  private Collection<Project> ensureConsistentMaster(Project parent, Collection<Project> children) {
    children.forEach(child -> child.setMasterProject(parent.getMasterProject()));
    return IterableUtils.toList(projectRepository.saveAll(children));
  }
}
