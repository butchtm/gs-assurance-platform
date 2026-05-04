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
public class ConsultationNotificationProcess {

  private final ProcessService processService;
  private final ConsultationNotificationJobRunPolicy runPolicy;
  private final ConsultationNotificationService notificationService;
  private final User systemUser;

  @Scheduled(
      cron = "${stakeholder-consultation.send-notification.check-cron}",
      zone = "Europe/London")
  public void sendConsultationNotificationsIfRequired() {
    if (!runPolicy.shouldRun()) {
      log.debug("Consultation Notification process not required so not running");
      return;
    }
    runNotificationSendingProcess();
  }

  public void runNotificationSendingProcess() {
    var process = processService.recordConsultationNotificationJobProcessStart();
    try {
      notificationService.sendConsultationNotifications(systemUser);
    } finally {
      processService.recordProcessComplete(process);
    }
  }
}
