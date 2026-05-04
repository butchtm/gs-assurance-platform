package org.goldstandard.assurance.api.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationNotificationProcess {

  private final ProcessService processService;
  private final ConversationNotificationJobRunPolicy runPolicy;
  private final ScheduledConversationNotificationService notificationService;
  private final User systemUser;

  @Scheduled(cron = "${conversation.send-notification.check-cron}", zone = "Europe/London")
  public void sendConversationNotificationsIfRequired() {
    if (!runPolicy.shouldRun()) {
      log.debug("Conversation Notification process not required so not running");
      return;
    }
    runNotificationSendingProcess();
  }

  public void runNotificationSendingProcess() {
    var process = processService.recordConversationNotificationJobProcessStart();
    try {
      notificationService.sendConversationNotifications(systemUser);
    } finally {
      processService.recordProcessComplete(process);
    }
  }
}
