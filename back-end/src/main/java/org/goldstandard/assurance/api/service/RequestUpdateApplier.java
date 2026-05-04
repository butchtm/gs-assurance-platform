package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.Request.RequestBuilder;
import org.goldstandard.assurance.api.entity.RequestCancellationReason;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.activity.ActivityEnder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestUpdateApplier {

  private final AuthenticatedUserSupplier authenticatedUserSupplier;
  private final RequestMethodologyFactory methodologyFactory;
  private final RequestDraftCreditingPeriodFactory draftCreditingPeriodFactory;
  private final RequestDraftMonitoringPeriodFactory draftMonitoringPeriodFactory;
  private final ActivityEnder activityEnder;
  private final Clock clock;

  public Request applyUpdates(Request originalRequest, RequestCreateUpdateDTO update) {
    if (originalRequest.isBatchChild()) {
      return applyChildUpdates(originalRequest, update);
    }
    return applyAllUpdates(originalRequest, update);
  }

  public Request applyParentUpdates(Request request, RequestCreateUpdateDTO update) {
    RequestBuilder builder = request.toBuilder();
    var authenticatedUserId = authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId();
    builder.updatedByUserId(authenticatedUserId);
    if (update.getStartDate() != null) {
      builder.startDate(update.getStartDate());
    }
    if (update.getManuallyAdded() != null) {
      builder.manuallyAdded(update.getManuallyAdded());
    }
    if (update.getRequestStatusId() != null) {
      var requestStatus = new RequestStatus(update.getRequestStatusId());
      builder.currentRequestStatus(requestStatus);
      if (requestStatus.isInactive() && Objects.isNull(request.getEndDate())) {
        builder.endDate(ZonedDateTime.now(clock));
      }
    }
    if (isRequestStatusChanged(request, update)) {
      builder.submittedByUserId(authenticatedUserId);
    }
    if (update.getFastTrack() != null) {
      builder.fastTracked(update.getFastTrack());
    }
    if (update.getInternal() != null) {
      builder.internal(update.getInternal());
    }
    if (update.getCancellationReasonId() != null) {
      builder.cancellationReason(new RequestCancellationReason(update.getCancellationReasonId()));
      if (update.getOtherCancellationReason() != null) {
        builder.otherCancellationReason(update.getOtherCancellationReason());
      }
    }
    return activityEnder.endCurrentActivityIfRequired(request, builder.build());
  }

  private Request applyAllUpdates(Request request, RequestCreateUpdateDTO update) {
    var updated = applyParentUpdates(request, update);
    return applyChildUpdates(updated, update);
  }

  private Request applyChildUpdates(Request request, RequestCreateUpdateDTO update) {
    RequestBuilder builder = request.toBuilder();
    builder.updatedByUserId(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId());
    if (update.getCreditingPeriod() != null) {
      builder.creditingPeriod(update.getCreditingPeriod().getPeriodId()).draftCreditingPeriod(null);
    }
    if (update.containsNewCreditingPeriod()) {
      draftCreditingPeriodFactory
          .build(request.getId(), update.getCreditingPeriod())
          .ifPresent(
              draftCreditingPeriod ->
                  builder.draftCreditingPeriod(draftCreditingPeriod).creditingPeriod(null));
    }
    var monitoringPeriod = update.getMonitoringPeriod();
    if (monitoringPeriod != null) {
      if (monitoringPeriod.getId() != null) {
        builder.monitoringPeriodId(monitoringPeriod.getId()).draftMonitoringPeriod(null);
      }
      if (update.containsNewMonitoringPeriod()) {
        draftMonitoringPeriodFactory
            .build(request, monitoringPeriod)
            .ifPresent(
                draftMonitoringPeriod ->
                    builder.draftMonitoringPeriod(draftMonitoringPeriod).monitoringPeriodId(null));
      } else if (update.containsUpdatesToMonitoringPeriod(request)) {
        draftMonitoringPeriodFactory
            .build(request, monitoringPeriod)
            .ifPresent(builder::draftMonitoringPeriod);
      }
    }
    if (update.getEmissionReductionAmount() != null) {
      builder.emissionReductionAmount(update.getEmissionReductionAmount());
    }
    if (update.getMethodologies() != null) {
      builder.requestMethodologies(
          methodologyFactory.toMethodologies(update.getMethodologies(), request));
    }

    if (update.hasMethodologies()) {
      builder.requestMethodologies(
          methodologyFactory.toMethodologies(update.getMethodologies(), request));
    }
    return builder.build();
  }

  private boolean isRequestStatusChanged(Request request, RequestCreateUpdateDTO update) {
    return !Objects.equals(request.getCurrentRequestStatus().getId(), update.getRequestStatusId());
  }
}
