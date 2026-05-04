package org.goldstandard.assurance.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Ngo;

@RequiredArgsConstructor
public class NotificationRecipientNgo implements NotificationRecipient {

  private final Ngo ngo;

  public String getType() {
    return "NGO";
  }

  public Integer getId() {
    return ngo.getId();
  }

  public String getName() {
    return ngo.getName();
  }

  public String getEmailAddress() {
    return ngo.getEmailAddress();
  }
}
