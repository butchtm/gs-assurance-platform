package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeScheduleRepository extends JpaRepository<FeeSchedule, Integer> {
  // intentionally blank
}
