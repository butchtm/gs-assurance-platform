package org.goldstandard.assurance.api.service.notification;

import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.document.PresignedUrlResult;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.DownloadModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportNotificationService {

  public static final TemplateKey TEMPLATE_KEY = new TemplateKey("REPORT_GENERATED", "v0.0.1");

  private final NotificationService notificationService;

  public void sendReportNotification(User user, PresignedUrlResult result) {
    var model = toNotificationModel(result);
    notificationService.sendUserNotification(TEMPLATE_KEY, model, user, user);
  }

  private static NotificationModel toNotificationModel(PresignedUrlResult result) {
    return NotificationModel.builder().download(toDownloadModel(result)).build();
  }

  private static DownloadModel toDownloadModel(PresignedUrlResult result) {
    var expiry = result.getExpiry();
    return DownloadModel.builder()
        .presignedUrl(result.getUrl())
        .key(result.getKey())
        .checksum(result.getChecksum())
        .expiryDate(expiry.format(DateTimeFormatter.ofPattern("dd MMMM")))
        .expiryTime(expiry.format(DateTimeFormatter.ofPattern("HH:mm z")))
        .build();
  }
}
