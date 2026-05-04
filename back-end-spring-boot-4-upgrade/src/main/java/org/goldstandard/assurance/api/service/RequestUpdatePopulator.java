package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.isInAssignment;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestUpdatePopulator {

  public RequestCreateUpdateDTO populateDefaultsIfRequired(
      Request originalRequest, RequestCreateUpdateDTO update) {
    var builder = update.toBuilder();
    if (shouldDefaultFastTrackTrue(originalRequest, update)) {
      builder.fastTrack(true);
    }
    if (shouldDefaultInternalTrue(originalRequest, update)) {
      builder.internal(true);
    }
    return builder.build();
  }

  private static boolean shouldDefaultFastTrackTrue(
      Request originalRequest, RequestCreateUpdateDTO update) {
    return Objects.isNull(originalRequest.getFastTracked())
        && Objects.isNull(update.getFastTrack())
        && originalRequest.isPreliminaryReview()
        && isInAssignment(update.getRequestStatusId());
  }

  private static boolean shouldDefaultInternalTrue(
      Request originalRequest, RequestCreateUpdateDTO update) {
    return Objects.isNull(update.getInternal())
        && Objects.isNull(originalRequest.getInternal())
        && !originalRequest.isFastTracked()
        && !update.isFastTrack()
        && isInAssignment(update.getRequestStatusId());
  }
}
