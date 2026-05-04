package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.goldstandard.assurance.api.entity.view.ReferenceDataViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceDataViewRepository
    extends JpaRepository<ReferenceDataView, ReferenceDataViewId> {}
