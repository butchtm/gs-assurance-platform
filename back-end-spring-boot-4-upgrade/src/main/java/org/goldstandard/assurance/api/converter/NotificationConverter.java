package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.NotificationDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter {

  public PagedResource<NotificationDTO> toPagedResource(Page<Notification> page) {
    return new PagedResource<>(page.map(this::toDTO));
  }

  private NotificationDTO toDTO(Notification notification) {
    return NotificationDTO.builder()
        .id(notification.getId())
        .recipientType(notification.getRecipientType())
        .recipientId(notification.getRecipientId())
        .addressees(notification.getAddressees())
        .subject(notification.getSubject())
        .htmlBody(notification.getHtmlBody())
        .textBody(notification.getTextBody())
        .createdTimestamp(notification.getCreatedTimestamp())
        .messagingReference(notification.getMessagingReference())
        .build();
  }
}
