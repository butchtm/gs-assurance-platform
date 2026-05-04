package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectClassificationRepository
    extends CrudRepository<ProjectClassification, Integer> {

  default ProjectClassification forceFindById(Integer id) {
    return findById(id)
        .orElseThrow(
            () -> new NotFoundException("project classification with id %d not found", id));
  }
}
