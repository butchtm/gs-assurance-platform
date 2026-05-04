package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.UserBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBatchRepository
    extends JpaRepository<UserBatch, Integer>, JpaSpecificationExecutor<UserBatch> {}
