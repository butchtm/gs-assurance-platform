package org.goldstandard.assurance.api.service.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.repository.notification.NotificationRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveNotificationTransaction {

  private final NotificationRepository repository;
  private final AuditService auditService;
  private final NotificationAuditInfoFactory auditInfoFactory;

  @Transactional
  public void recordNotificationSent(
      Notification notification, NotificationTemplate template, User authenticatedUser) {
    var savedNotification = repository.save(notification);
    var auditInfo = auditInfoFactory.build(savedNotification, template.baseName());
    auditService.auditEvent(AuditEventSubject.NOTIFICATION_SENT, auditInfo, authenticatedUser);
  }
}
