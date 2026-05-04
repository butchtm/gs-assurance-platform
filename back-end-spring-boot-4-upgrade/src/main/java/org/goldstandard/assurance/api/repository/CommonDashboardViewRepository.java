package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDashboardViewRepository
    extends PagingAndSortingRepository<CommonDashboardView, Integer>,
        JpaSpecificationExecutor<CommonDashboardView> {
  // intentionally blank
}
