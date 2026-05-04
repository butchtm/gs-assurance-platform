package org.goldstandard.assurance.api.converter;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestDTO;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestInvoiceDTO;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestMonitoringPeriodDTO;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestProjectDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommonDashboardConverter {

  private final InvoiceStatusConverter invoiceStatusConverter;
  private final ProjectStatusConverter projectStatusConverter;
  private final RequestStatusConverter requestStatusConverter;
  private final ProjectClassificationConverter projectClassificationConverter;
  private final FeeScheduleConverter feeScheduleConverter;

  public PagedResource<CommonDashboardRequestDTO> toPagedResource(Page<CommonDashboardView> page) {
    return new PagedResource<>(page.map(this::toDTO));
  }

  private CommonDashboardRequestDTO toDTO(CommonDashboardView view) {
    return CommonDashboardRequestDTO.builder()
        .id(view.getRequestId())
        .name(view.getRequestName())
        .requestStatus(
            Optional.ofNullable(view.getRequestStatus())
                .map(requestStatusConverter::toDTO)
                .orElse(null))
        .startDate(view.getRequestStartDate())
        .closedDate(view.getRequestEndDate())
        .currentRoundIndex(view.getCurrentRoundIndex())
        .firstRoundStartDate(view.getFirstRoundStartDate())
        .currentRoundEndDate(view.getCurrentRoundEndDate())
        .monitoringPeriod(toRequestMonitoringPeriodDTO(view))
        .projectTypesString(view.getProjectTypesString())
        .project(toProjectDTO(view))
        .invoice(toInvoiceDTO(view))
        .build();
  }

  private CommonDashboardRequestProjectDTO toProjectDTO(CommonDashboardView view) {
    return CommonDashboardRequestProjectDTO.builder()
        .gsid(view.getProjectGsid())
        .name(view.getProjectName())
        .status(
            Optional.ofNullable(view.getProjectStatus())
                .map(projectStatusConverter::toDTO)
                .orElse(null))
        .types(view.getProjectTypes())
        .classification(
            Optional.ofNullable(view.getProjectClassification())
                .map(projectClassificationConverter::toDTO)
                .orElse(null))
        .poaGsid(view.getMasterGsid())
        .realCaseVpaGsid(view.getRealCaseGsid())
        .accountName(view.getAccountName())
        .feeSchedule(feeScheduleConverter.toDTO(view.getProjectFeeSchedule()))
        .build();
  }

  private CommonDashboardRequestInvoiceDTO toInvoiceDTO(CommonDashboardView view) {
    return new CommonDashboardRequestInvoiceDTO(
        Optional.ofNullable(view.getInvoiceStatus())
            .map(invoiceStatusConverter::toDTO)
            .orElse(null));
  }

  private CommonDashboardRequestMonitoringPeriodDTO toRequestMonitoringPeriodDTO(
      CommonDashboardView view) {
    var period = view.getMonitoringPeriod();
    if (Objects.isNull(period)) {
      return null;
    }
    return CommonDashboardRequestMonitoringPeriodDTO.builder()
        .id(period.getId())
        .feeSchedule(feeScheduleConverter.toDTO(view.getRequestFeeSchedule()))
        .build();
  }
}
