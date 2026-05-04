package org.goldstandard.assurance.api.repository.notification;

import org.goldstandard.assurance.api.entity.notification.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NotificationRepository
    extends CrudRepository<Notification, Integer>,
        PagingAndSortingRepository<Notification, Integer> {

  // intentionally blank

}
