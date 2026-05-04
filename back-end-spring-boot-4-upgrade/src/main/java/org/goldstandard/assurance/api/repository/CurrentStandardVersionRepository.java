package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentStandardVersionRepository
    extends CrudRepository<CurrentStandardVersion, Integer> {}
