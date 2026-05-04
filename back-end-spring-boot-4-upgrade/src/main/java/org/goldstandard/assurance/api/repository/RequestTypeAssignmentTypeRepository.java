package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.RequestTypeAssignmentType;
import org.goldstandard.assurance.api.entity.lookup.RequestTypeAssignmentTypeId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTypeAssignmentTypeRepository
    extends CrudRepository<RequestTypeAssignmentType, RequestTypeAssignmentTypeId> {

  @Query
  List<RequestTypeAssignmentType> findAllByRequestTypeId(Integer requestTypeId);
}
