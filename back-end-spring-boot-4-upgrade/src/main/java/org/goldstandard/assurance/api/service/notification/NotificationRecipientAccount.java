package org.goldstandard.assurance.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;

@RequiredArgsConstructor
public class NotificationRecipientAccount implements NotificationRecipient {

  private final Account account;

  public String getType() {
    return "ACCOUNT";
  }

  public Integer getId() {
    return account.getId();
  }

  public String getName() {
    return account.getName();
  }

  public String getEmailAddress() {
    return account.getEmailAddress();
  }
}
