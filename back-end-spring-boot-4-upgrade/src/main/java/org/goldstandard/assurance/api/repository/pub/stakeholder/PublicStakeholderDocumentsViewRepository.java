package org.goldstandard.assurance.api.repository.pub.stakeholder;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestDocumentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicStakeholderDocumentsViewRepository
    extends JpaRepository<RequestDocumentView, String> {
  List<RequestDocumentView> findByRequestId(Integer requestId);
}
