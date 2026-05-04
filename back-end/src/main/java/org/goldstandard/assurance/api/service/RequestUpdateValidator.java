package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestUpdateValidator {

  private final RequestAccessPolicy accessPolicy;

  public void validate(Request request, RequestCreateUpdateDTO update, Integer projectId) {
    validateRequestBelongsToProject(request, projectId);
    accessPolicy.validateCanUpdateRequest(request);
    if (request.isBatchChild()) {
      validateBatchChildRequestUpdate(request.getId(), update);
    }
    validateFastTrack(request, update);
    validateInternal(update);
  }

  private void validateRequestBelongsToProject(Request request, Integer projectId) {
    if (!request.getProject().getId().equals(projectId)) {
      throw new BadRequestException(
          "Request id {} does not belong to project id {}.", request.getId(), projectId);
    }
  }

  private void validateFastTrack(Request request, RequestCreateUpdateDTO update) {
    if (update.isFastTrack() && !request.isPreliminaryReview()) {
      throw new BadRequestException(
          "Cannot fast track request as is not of type 'Preliminary Review'");
    }
  }

  private void validateInternal(RequestCreateUpdateDTO update) {
    if ((update.getInternal() != null) && update.isFastTrack()) {
      throw new BadRequestException("Cannot set internal/external flag on a fast tracked request");
    }
  }

  private void validateBatchChildRequestUpdate(Integer id, RequestCreateUpdateDTO update) {
    List<String> validationFailures = new ArrayList<>();
    if (update.getStartDate() != null) {
      validationFailures.add("start date");
    }
    if (update.getManuallyAdded() != null) {
      validationFailures.add("manually added");
    }
    if (update.getRequestStatusId() != null) {
      validationFailures.add("request status");
    }
    if (update.getStartStakeholderConsultation() != null) {
      validationFailures.add("stakeholder consultation");
    }
    if (update.getFastTrack() != null) {
      validationFailures.add("fast track");
    }
    if (!validationFailures.isEmpty()) {
      throw new BatchRequestException(
          "Updates: [%s], are not permitted for batch child request %d",
          String.join(", ", validationFailures), id);
    }
  }
}
