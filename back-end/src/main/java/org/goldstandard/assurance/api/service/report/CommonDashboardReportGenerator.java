package org.goldstandard.assurance.api.service.report;

import java.time.Clock;
import java.util.concurrent.RejectedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.service.CommonDashboardFinder;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.notification.ReportNotificationService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonDashboardReportGenerator {

  private final AuthenticatedUserSupplier userSupplier;
  private final CommonDashboardFinder finder;
  private final FileStore fileStore;
  private final ReportKeyFactory keyFactory;
  private final Clock clock;
  private final ReportNotificationService notificationService;
  private final ThreadPoolTaskExecutor taskExecutor;

  @Autowired
  public CommonDashboardReportGenerator(
      AuthenticatedUserSupplier userSupplier,
      CommonDashboardFinder finder,
      ReportKeyFactory keyFactory,
      Clock clock,
      @Qualifier("downloadFileStore") FileStore fileStore,
      ReportNotificationService notificationService,
      ThreadPoolTaskExecutor taskExecutor) {
    this.userSupplier = userSupplier;
    this.finder = finder;
    this.keyFactory = keyFactory;
    this.clock = clock;
    this.fileStore = fileStore;
    this.notificationService = notificationService;
    this.taskExecutor = taskExecutor;
  }

  public void generate(CommonDashboardRequestFiltersDTO filters, Pageable pageable) {
    log.info("generating report using filters {}", filters);
    var task =
        CommonDashboardReportRunnable.builder()
            .user(userSupplier.forceGetAuthenticatedUser())
            .fileWriter(new CommonDashboardReportWriter(finder, filters, pageable))
            .keyFactory(keyFactory)
            .clock(clock)
            .fileStore(fileStore)
            .notificationService(notificationService)
            .build();
    try {
      taskExecutor.execute(task);
      log.info("task execution triggered");
    } catch (RejectedExecutionException e) {
      log.error("report generation task rejected {}", e.getMessage(), e);
    }
  }
}
