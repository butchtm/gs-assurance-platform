package org.goldstandard.assurance.api.repository.notification;

import java.util.List;
import org.goldstandard.assurance.api.entity.notification.RequestStatusNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RequestStatusNotificationRepository
    extends CrudRepository<RequestStatusNotification, Integer> {

  // Exact match on target status
  // Containing range for round of review
  // Exact match on source status trumps wild card (i.e. null) source status
  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT
                      *
                    FROM
                      gs.request_status_notification
                    WHERE
                      to_status_id = :toStatusId AND
                      min_round_index <= :roundIndex AND
                      max_round_index >= :roundIndex AND
                       (
                         from_status_id = :fromStatusId OR
                         from_status_id IS NULL
                       )
                    ORDER BY from_status_id NULLS LAST
                    """)
  List<RequestStatusNotification> findNotificationsForTransition(
      Integer fromStatusId, Integer toStatusId, Integer roundIndex);
}
