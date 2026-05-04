package org.goldstandard.assurance.api.fixtures;

import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationParams;

public class NotificationParamsMother {

  public static NotificationParams build() {
    return NotificationParams.builder()
        .template(template())
        .model(model())
        .recipient(NotificationRecipientMother.build())
        .build();
  }

  private static NotificationTemplate template() {
    return NotificationTemplate.builder().build();
  }

  private static NotificationModel model() {
    return new NotificationModel();
  }
}
