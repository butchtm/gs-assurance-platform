package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriodId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCreditingMonitoringPeriodRepository
    extends JpaRepository<ProjectCreditingMonitoringPeriod, ProjectCreditingMonitoringPeriodId> {
  // intentionally blank
}
