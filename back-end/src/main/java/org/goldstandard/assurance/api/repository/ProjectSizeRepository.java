package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSizeRepository extends CrudRepository<ProjectSize, Integer> {}
