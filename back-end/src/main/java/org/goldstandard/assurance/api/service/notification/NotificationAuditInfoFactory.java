package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_NOTIFICATION_ID;
import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_NOTIFICATION_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationAuditInfoFactory {

  public Map<String, Object> build(Notification notification, String notificationType) {
    Map<String, Object> info = new HashMap<>();
    Optional.ofNullable(notification.getRecipientType())
        .ifPresent(id -> info.put("recipientType", id));
    Optional.ofNullable(notification.getRecipientId())
        .ifPresent(type -> info.put("recipientId", type));
    Optional.ofNullable(notificationType)
        .ifPresent(type -> info.put(AUDIT_INFO_NOTIFICATION_TYPE, type));
    Optional.ofNullable(notification.getId())
        .ifPresent(id -> info.put(AUDIT_INFO_NOTIFICATION_ID, id));
    return info;
  }
}
