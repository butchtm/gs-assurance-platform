package org.goldstandard.assurance.api.service.finance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.FeeScheduleRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FinanceInvoiceTypeDiscountApplier {

  private final FeeScheduleRepository repository;

  public String applyDiscountsIfRequired(Request request, FinanceInvoiceType type) {
    var label = type.getLabel();
    var project = request.getProject();
    if (request.hasIssuanceType()) {
      return applyIssuanceDiscount(label, project);
    }
    if (project.isRegularCaseVpa()) {
      return applyRegularCaseVpaDiscount(label);
    }
    if (project.isMicroscale()) {
      return applyMicroscaleDiscount(label);
    }
    return label;
  }

  private String applyIssuanceDiscount(String invoiceType, Project project) {
    var discount = toIssuanceDiscount(project);
    return String.format("%s %s", invoiceType, discount).trim();
  }

  private String toIssuanceDiscount(Project project) {
    var feeScheduleDiscount = toFeeScheduleDiscount(project);
    if (project.inLeastDevelopedCountry()) {
      return String.format("%s LDC", feeScheduleDiscount).trim();
    }
    return feeScheduleDiscount;
  }

  private Optional<FeeSchedule> toLatestFeeSchedule(Project project) {
    return project.getLatestFeeScheduleId().flatMap(repository::findById);
  }

  private String toFeeScheduleDiscount(Project project) {
    return toLatestFeeSchedule(project)
        .map(feeSchedule -> toFeeScheduleDiscount(project, feeSchedule))
        .orElseThrow(
            () ->
                new NotFoundException("fee schedule not found for project %s", project.getGsid()));
  }

  private String toFeeScheduleDiscount(Project project, FeeSchedule feeSchedule) {
    return switch (feeSchedule.getCode()) {
      case "CASH_PER_CREDIT" -> "CASH";
      case "SHARE_OF_PROCEEDS" -> "SoP";
      default ->
          throw new DataException(
              "fee schedule code %s for project %s not mapped",
              feeSchedule.getCode(), project.getGsid());
    };
  }

  private String applyRegularCaseVpaDiscount(String invoiceType) {
    return String.format("%s Regular VPA", invoiceType);
  }

  private String applyMicroscaleDiscount(String invoiceType) {
    return String.format("%s Microscale", invoiceType);
  }
}
