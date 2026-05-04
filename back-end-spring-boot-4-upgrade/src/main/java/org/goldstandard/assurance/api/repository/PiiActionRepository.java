package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiiActionRepository extends CrudRepository<PiiAction, Integer> {}
