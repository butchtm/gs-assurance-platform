package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.AllRequestView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllRequestViewRepository
    extends PagingAndSortingRepository<AllRequestView, Integer>,
        JpaSpecificationExecutor<AllRequestView> {
  // intentionally blank
}
