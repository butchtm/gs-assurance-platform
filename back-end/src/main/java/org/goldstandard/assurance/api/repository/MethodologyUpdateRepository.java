package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MethodologyUpdateRepository
    extends JpaRepository<DbMethodologyUpdate, DbMethodologyUpdateId> {}
