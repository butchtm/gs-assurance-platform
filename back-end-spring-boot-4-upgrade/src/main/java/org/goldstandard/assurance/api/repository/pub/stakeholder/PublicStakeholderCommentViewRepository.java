package org.goldstandard.assurance.api.repository.pub.stakeholder;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestCommentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderCommentViewRepository
    extends JpaRepository<RequestCommentView, Integer> {
  List<RequestCommentView> findByRequestId(Integer requestId);
}
