package org.goldstandard.assurance.api.service.notification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.repository.notification.NotificationRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveNotificationTransactionTest {

  @InjectMocks SaveNotificationTransaction transaction;

  @Mock NotificationRepository repository;

  @Mock AuditService auditService;

  @Mock NotificationAuditInfoFactory auditInfoFactory;

  @Test
  void shouldSaveNotificationAndAuditNotificationSaved() {
    var notification = new Notification();
    var savedNotification = new Notification();
    when(repository.save(notification)).thenReturn(savedNotification);
    var template = NotificationTemplate.builder().name("template-name").version("1.0").build();
    var auditInfo = Map.of("key1", new Object());
    when(auditInfoFactory.build(savedNotification, template.baseName())).thenReturn(auditInfo);
    var authenticatedUser = new User();

    transaction.recordNotificationSent(notification, template, authenticatedUser);

    verify(auditService)
        .auditEvent(AuditEventSubject.NOTIFICATION_SENT, auditInfo, authenticatedUser);
  }
}
