package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.pii.PiiFinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiiFindingRepository extends JpaRepository<PiiFinding, Integer> {

  List<PiiFinding> findByPiiScanRunId(Integer scanRunId);

  void deleteByPiiScanRunId(Integer scanRunId);
}
