package org.goldstandard.assurance.api.repository.pub;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.PublicRequestsView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicRequestsViewRepository extends JpaRepository<PublicRequestsView, Integer> {
  List<PublicRequestsView> findByGsidOrderByRequestIndexAscRequestIdAsc(String gsid);
}
