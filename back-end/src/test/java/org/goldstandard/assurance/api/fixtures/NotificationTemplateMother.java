package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.service.notification.TemplateKey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationTemplateMother {

  public static NotificationTemplate build(TemplateKey key) {
    return builder(key).build();
  }

  public static NotificationTemplate withoutSubject(TemplateKey key) {
    return builder(key).subject(null).build();
  }

  public static NotificationTemplate withoutHtmlBody(TemplateKey key) {
    return builder(key).htmlBody(null).build();
  }

  public static NotificationTemplate withoutTextBody(TemplateKey key) {
    return builder(key).textBody(null).build();
  }

  public static NotificationTemplate.NotificationTemplateBuilder builder(TemplateKey key) {
    return NotificationTemplate.builder()
        .name(key.name())
        .version(key.version())
        .subject("Subject ${subject}")
        .htmlBody("HTML Body ${content}")
        .textBody("Text Body ${content}");
  }
}
