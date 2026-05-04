package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.Bundle;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository
    extends CrudRepository<Bundle, Integer>,
        JpaSpecificationExecutor<Bundle>,
        PagingAndSortingRepository<Bundle, Integer> {
  // Intentionally Blank

}
