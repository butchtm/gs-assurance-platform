package org.goldstandard.assurance.api.repository.notification;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.notification.AssignmentStatusNotification;
import org.springframework.data.repository.CrudRepository;

public interface AssignmentNotificationRepository
    extends CrudRepository<AssignmentStatusNotification, Integer> {

  Optional<AssignmentStatusNotification> findNotificationByAssignmentTypeIdAndAssignmentStatusId(
      Integer assignmentType, Integer assignmentStatus);
}
