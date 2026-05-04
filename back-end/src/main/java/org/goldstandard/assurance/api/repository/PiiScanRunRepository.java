package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiiScanRunRepository extends JpaRepository<PiiScanRun, Integer> {}
