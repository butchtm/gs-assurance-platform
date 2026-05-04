package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.notification.DocumentPiiNotificationService;
import org.goldstandard.assurance.api.service.notification.RequestNotificationService;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestUpdateNotificationSender {

  private final RequestFinder finder;
  private final RequestNotificationService requestNotificationService;
  private final DocumentPiiNotificationService documentNotificationService;
  private final RequestStatusChecker statusChecker;

  public void sendUpdateNotifications(Request originalRequest) {
    var updatedRequest = finder.getRequestById(originalRequest.getId());
    var originalStatus = originalRequest.getCurrentRequestStatus();
    requestNotificationService.sendRequestNotification(updatedRequest, originalStatus.getId());
    if (statusChecker.movedBackToInProgressOrInAuditFromCompletionCheck(
        originalStatus, updatedRequest.getCurrentRequestStatus())) {
      documentNotificationService.sendDocumentPiiNotification(originalRequest);
    }
  }
}
