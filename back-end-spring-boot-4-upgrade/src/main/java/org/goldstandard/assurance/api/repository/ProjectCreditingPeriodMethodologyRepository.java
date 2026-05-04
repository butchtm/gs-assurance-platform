package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodologyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCreditingPeriodMethodologyRepository
    extends JpaRepository<ProjectCreditingPeriodMethodology, ProjectCreditingPeriodMethodologyId> {

  boolean existsByMethodology_Id(Integer methodologyId);

  Collection<ProjectCreditingPeriodMethodology> findAllByMethodology_Id(Integer methodologyId);
}
