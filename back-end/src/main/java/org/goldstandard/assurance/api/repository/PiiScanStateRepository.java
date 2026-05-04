package org.goldstandard.assurance.api.repository;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.pii.PiiScanState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiiScanStateRepository extends JpaRepository<PiiScanState, Integer> {

  Optional<PiiScanState> findByPiiScanRunId(Integer piiScanRunId);
}
