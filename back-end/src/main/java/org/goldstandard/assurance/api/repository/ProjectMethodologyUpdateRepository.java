package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMethodologyUpdateRepository
    extends JpaRepository<ProjectMethodologyUpdate, Integer>,
        JpaSpecificationExecutor<ProjectMethodologyUpdate> {

  List<ProjectMethodologyUpdate> getAllByRequest(Request request);
}
