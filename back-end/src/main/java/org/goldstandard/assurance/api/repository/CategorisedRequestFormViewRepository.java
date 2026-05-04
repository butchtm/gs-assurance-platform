package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.CategorisedRequestFormView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorisedRequestFormViewRepository
    extends JpaRepository<CategorisedRequestFormView, Integer>,
        JpaSpecificationExecutor<CategorisedRequestFormView> {

  // intentionally blank
}
