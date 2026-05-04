package org.goldstandard.assurance.api.repository.pub.stakeholder;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderRequestView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderRequestsViewRepository
    extends JpaRepository<StakeholderRequestView, Integer> {
  List<StakeholderRequestView> findByGsid(String gsid);
}
