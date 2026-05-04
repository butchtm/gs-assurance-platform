package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestStatusRepository extends CrudRepository<RequestStatus, Integer> {}
