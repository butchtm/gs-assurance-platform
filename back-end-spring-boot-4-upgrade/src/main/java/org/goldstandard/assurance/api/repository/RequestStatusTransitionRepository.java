package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.RequestStatusTransition;
import org.goldstandard.assurance.api.entity.lookup.RequestStatusTransitionId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestStatusTransitionRepository
    extends CrudRepository<RequestStatusTransition, RequestStatusTransitionId> {

  @Query
  List<RequestStatusTransition> findAllByRequestStatusId(Integer requestStatusId);
}
