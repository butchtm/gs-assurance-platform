package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCreditingPeriodRepository
    extends JpaRepository<ProjectCreditingPeriod, ProjectCreditingPeriodId> {
  List<ProjectCreditingPeriod> findAllByProjectId(Integer projectId);

  Optional<ProjectCreditingPeriod> findByProjectGsidAndPeriodId(
      String projectGsid, Integer periodId);

  Optional<ProjectCreditingPeriod> findByProjectIdAndPeriodId(Integer projectId, Integer periodId);

  void deleteAllByProjectId(Integer projectId);
}
