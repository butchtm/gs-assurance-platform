package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStatusRepository extends CrudRepository<ProjectStatus, Integer> {

  default ProjectStatus forceFindById(Integer id) {
    return findById(id)
        .orElseThrow(() -> new NotFoundException("project status with id %d not found", id));
  }
}
