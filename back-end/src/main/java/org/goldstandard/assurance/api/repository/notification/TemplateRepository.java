package org.goldstandard.assurance.api.repository.notification;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<NotificationTemplate, Integer> {
  Optional<NotificationTemplate> findByNameAndVersion(String name, String version);
}
