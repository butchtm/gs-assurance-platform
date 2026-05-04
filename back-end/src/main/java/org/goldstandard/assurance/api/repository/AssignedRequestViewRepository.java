package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignedRequestViewRepository
    extends PagingAndSortingRepository<AssignedRequestView, Integer>,
        JpaSpecificationExecutor<AssignedRequestView> {
  // intentionally blank
}
