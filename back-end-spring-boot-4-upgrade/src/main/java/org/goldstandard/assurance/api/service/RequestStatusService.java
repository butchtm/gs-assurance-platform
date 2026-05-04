package org.goldstandard.assurance.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestStatusDetail;
import org.goldstandard.assurance.api.repository.RequestStatusAccountTypeRepository;
import org.goldstandard.assurance.api.repository.RequestStatusRepository;
import org.goldstandard.assurance.api.repository.RequestStatusTransitionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestStatusService {

  private final RequestStatusTransitionRepository requestStatusTransitionRepository;
  private final RequestStatusAccountTypeRepository requestStatusAccountTypeRepository;
  private final RequestStatusRepository requestStatusRepository;

  public Optional<RequestStatusDetail> getDetailForRequestStatus(Integer requestStatusId) {
    if (!requestStatusRepository.existsById(requestStatusId)) {
      return Optional.empty();
    }
    return Optional.of(
        RequestStatusDetail.builder()
            .requestStatusId(requestStatusId)
            .permittedStatuses(
                requestStatusTransitionRepository.findAllByRequestStatusId(requestStatusId).stream()
                    .map(
                        requestStatusTransition ->
                            RequestStatusDetail.PermittedStatuses.builder()
                                .requestStatusId(
                                    requestStatusTransition.getPermittedRequestStatusId())
                                .minimumRoundOfReviewCount(
                                    requestStatusTransition.getMinimumRoundOfReviewCount())
                                .permittedDuringConsultation(
                                    requestStatusTransition.isPermittedDuringConsultation())
                                .build())
                    .toList())
            .activeAccountTypes(
                requestStatusAccountTypeRepository
                    .findAllByRequestStatusId(requestStatusId)
                    .stream()
                    .map(
                        requestStatusAccountType ->
                            RequestStatusDetail.InvolvedAccountTypes.builder()
                                .accountTypeId(requestStatusAccountType.getAccountTypeId())
                                .canProgress(requestStatusAccountType.getCanProgress())
                                .build())
                    .toList())
            .build());
  }
}
