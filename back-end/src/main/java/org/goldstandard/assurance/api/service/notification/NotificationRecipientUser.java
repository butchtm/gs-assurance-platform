package org.goldstandard.assurance.api.service.notification;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.User;

@Builder
@Getter
public class NotificationRecipientUser implements NotificationRecipient {

  private final User user;

  public String getType() {
    return "USER";
  }

  public Integer getId() {
    return user.getId();
  }

  public String getName() {
    return user.getFullName();
  }

  public String getEmailAddress() {
    return user.getEmailAddress();
  }
}
