package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.pii.ProjectIdPiiScanRun;
import org.goldstandard.assurance.api.enumeration.ProjectScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectIdPiiScanRunRepository extends JpaRepository<ProjectIdPiiScanRun, Long> {

  @Modifying
  @Query(
      nativeQuery = true,
      value =
          "UPDATE project_id_pii_scan_run "
              + "SET status = :status "
              + "WHERE pii_scan_run_id = :piiScanRunId "
              + "AND project_id = :projectId")
  void updateStatusByPiiScanRunAndProjectId(
      Integer piiScanRunId, Integer projectId, ProjectScanStatus status);
}
