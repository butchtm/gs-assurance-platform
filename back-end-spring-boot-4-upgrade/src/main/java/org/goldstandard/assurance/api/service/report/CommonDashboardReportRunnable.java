package org.goldstandard.assurance.api.service.report;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.notification.ReportNotificationService;

@Slf4j
@Builder
public class CommonDashboardReportRunnable implements Runnable {

  private final User user;
  private final FileWriter fileWriter;
  private final ReportKeyFactory keyFactory;
  private final Clock clock;
  private final FileStore fileStore;
  private final ReportNotificationService notificationService;

  @Override
  public void run() {
    log.info("report generation task starting execution");
    try (var outputStream = new ConvertibleOutputStream()) {
      fileWriter.write(outputStream);
      var key = keyFactory.buildCommonDashboardKey(user);
      var expiry = ZonedDateTime.now(clock).plusDays(2).withZoneSameInstant(ZoneId.of("UTC"));
      var result = fileStore.generatePresignedUrl(key, outputStream, expiry);
      log.info(
          "generated report for object {} sending notification to user {} with expiry {}",
          result.getKey(),
          user.getUserName(),
          expiry);
      notificationService.sendReportNotification(user, result);
    } catch (Exception e) {
      throw new CommonDashboardReportException(e);
    }
  }
}
