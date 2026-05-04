package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.goldstandard.assurance.api.service.report.CommonDashboardReportGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonDashboardService {

  private final CommonDashboardFinder finder;
  private final CommonDashboardReportGenerator generator;

  public Page<CommonDashboardView> getCommonDashboardPage(
      CommonDashboardRequestFiltersDTO filters, Pageable pageable) {
    return finder.getCommonDashboardPage(filters, pageable);
  }

  public void generateReport(CommonDashboardRequestFiltersDTO filters, Pageable pageable) {
    generator.generate(filters, pageable);
  }
}
