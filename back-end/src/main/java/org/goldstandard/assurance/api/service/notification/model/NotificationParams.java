package org.goldstandard.assurance.api.service.notification.model;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.service.notification.NotificationRecipient;

@Builder
@Getter
public class NotificationParams {
  private final NotificationTemplate template;
  private final NotificationModel model;
  private final NotificationRecipient recipient;
  private final String recipientAddressOverride;
  private final User authenticatedUser;

  public String getRecipientAddress() {
    if (StringUtils.isNotEmpty(recipientAddressOverride)) {
      return recipientAddressOverride;
    }
    return recipient.getEmailAddress();
  }

  public Optional<User> getAuthenticatedUser() {
    return Optional.ofNullable(authenticatedUser);
  }
}
