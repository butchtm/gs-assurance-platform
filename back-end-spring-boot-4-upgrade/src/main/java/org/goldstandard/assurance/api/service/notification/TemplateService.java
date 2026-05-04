package org.goldstandard.assurance.api.service.notification;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.notification.TemplateRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TemplateService {

  private final TemplateRepository templateRepository;

  public NotificationTemplate forceGetTemplate(TemplateKey key) {
    return getNotificationTemplate(key)
        .orElseThrow(() -> new NotFoundException(toNotFoundMessage(key)));
  }

  public Optional<NotificationTemplate> getNotificationTemplate(TemplateKey key) {
    return templateRepository.findByNameAndVersion(key.name(), key.version());
  }

  private static String toNotFoundMessage(TemplateKey key) {
    return String.format("Template not found with key [%s].", key.fullName());
  }
}
