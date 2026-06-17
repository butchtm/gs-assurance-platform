package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Integer> {}
