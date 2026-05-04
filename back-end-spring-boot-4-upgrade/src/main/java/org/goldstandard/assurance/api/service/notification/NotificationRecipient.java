package org.goldstandard.assurance.api.service.notification;

public interface NotificationRecipient {

  String getType();

  Integer getId();

  String getName();

  String getEmailAddress();
}
