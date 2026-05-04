package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.view.BatchParentIdView;
import org.goldstandard.assurance.api.entity.view.BatchParentIdViewId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchParentIdViewRepository
    extends CrudRepository<BatchParentIdView, BatchParentIdViewId> {

  default Optional<BatchParentIdView> getBatchParentIdView(Integer requestId) {
    return findByRequestId(requestId).stream().min(BatchParentIdView.parentsFirst());
  }

  Collection<BatchParentIdView> findByRequestId(Integer requestId);

  Optional<BatchParentIdView> findByProjectIdAndRequestId(Integer projectId, Integer requestId);
}
