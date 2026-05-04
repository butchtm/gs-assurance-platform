package org.goldstandard.assurance.api.repository.pub;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.PublicDocumentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicDocumentViewRepository extends JpaRepository<PublicDocumentView, Integer> {
  List<PublicDocumentView> findByGsidAndRequestId(String gsid, Integer requestId);
}
