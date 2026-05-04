package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesViewId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadMessagesViewRepository
    extends CrudRepository<UnreadMessagesView, UnreadMessagesViewId>,
        JpaSpecificationExecutor<UnreadMessagesView> {

  Optional<UnreadMessagesView> findByUserIdAndRequestId(Integer userId, Integer requestId);

  Optional<Collection<UnreadMessagesView>> findAllByUserId(Integer userId);
}
