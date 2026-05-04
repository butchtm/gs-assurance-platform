package org.goldstandard.assurance.api.repository.pub.stakeholder;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderBatchRequestView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderBatchRequestsViewRepository
    extends JpaRepository<StakeholderBatchRequestView, Integer> {
  List<StakeholderBatchRequestView> findAllByGsidAndParentRequestId(String gsid, Integer requestId);
}
