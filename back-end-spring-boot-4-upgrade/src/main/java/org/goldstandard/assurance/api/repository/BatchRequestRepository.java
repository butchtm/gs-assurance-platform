package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRequestRepository extends JpaRepository<BatchRequest, Integer> {

  List<BatchRequest> findAllByBatchId(Integer batchId);
}
