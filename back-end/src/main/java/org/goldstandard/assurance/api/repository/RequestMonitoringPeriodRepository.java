package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestMonitoringPeriodRepository
    extends JpaRepository<RequestMonitoringPeriod, Integer> {}
