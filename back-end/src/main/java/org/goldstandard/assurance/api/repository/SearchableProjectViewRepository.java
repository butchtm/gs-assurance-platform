package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.SearchableProjectView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchableProjectViewRepository
    extends PagingAndSortingRepository<SearchableProjectView, Integer>,
        JpaSpecificationExecutor<SearchableProjectView> {

  // intentionally blank
}
