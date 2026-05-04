package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import org.goldstandard.assurance.api.entity.view.ProjectTreeNodeView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTreeNodeViewRepository
    extends CrudRepository<ProjectTreeNodeView, Integer>,
        JpaSpecificationExecutor<ProjectTreeNodeView> {

  default ProjectTreeNodeView forceGetById(Integer id) {
    return findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Project with id %s not found", id)));
  }

  Collection<ProjectTreeNodeView> findAllByMasterProjectId(Integer masterProjectId);
}
