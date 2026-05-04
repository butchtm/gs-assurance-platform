package org.goldstandard.assurance.api.repository.pub.stakeholder;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.BatchProjectView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderBatchProjectsViewRepository
    extends JpaRepository<BatchProjectView, String> {

  List<BatchProjectView> findAllByBatchId(Integer batchId);
}
