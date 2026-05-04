package org.goldstandard.assurance.api.service.notification;

import org.goldstandard.assurance.api.configuration.RendererConfiguration;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.exception.RendererException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RendererService {

  @Autowired private RendererConfiguration rendererConfiguration;

  @Autowired private TemplateService templateService;

  public RenderedMessage renderMessage(TemplateKey templateKey, Object model) {
    final NotificationTemplate notificationTemplate =
        templateService
            .getNotificationTemplate(templateKey)
            .orElseThrow(
                () ->
                    new RendererException(
                        "Notification template %s not found", templateKey.fullName()));
    return renderMessage(notificationTemplate, model);
  }

  public RenderedMessage renderMessage(NotificationTemplate notificationTemplate, Object model) {
    return RenderedMessage.renderMessage(
        rendererConfiguration.getRendererConfiguration(), notificationTemplate, model);
  }
}
