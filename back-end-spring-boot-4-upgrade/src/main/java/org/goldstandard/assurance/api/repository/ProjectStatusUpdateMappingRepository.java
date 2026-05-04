package org.goldstandard.assurance.api.repository;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.ProjectStatusUpdateMapping;
import org.goldstandard.assurance.api.entity.ProjectStatusUpdateMappingId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStatusUpdateMappingRepository
    extends CrudRepository<ProjectStatusUpdateMapping, ProjectStatusUpdateMappingId> {

  Optional<ProjectStatusUpdateMapping> findByRequestStatusIdAndRequestTypeId(
      int requestStatusId, int requestTypeId);
}
