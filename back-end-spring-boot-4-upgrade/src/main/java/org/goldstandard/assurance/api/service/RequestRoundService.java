package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.RequestRoundUpdateDTO;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestRoundService {

  private final RequestRoundRepository repository;
  private final RequestRoundTransaction transaction;

  public RequestRound updateRequestRound(
      Integer requestId, Integer index, RequestRoundUpdateDTO details) {
    log.info("updating request round {} for requestId {}", index, requestId);
    var round = findByRequestIdAndRoundIndex(requestId, index);
    round.setStatus(new RequestRoundStatus(details.getStatusId()));
    round.setStartDate(details.getStartDate());
    round.setEndDate(details.getEndDate());
    return transaction.updateRequestRound(round);
  }

  private RequestRound findByRequestIdAndRoundIndex(Integer requestId, Integer index) {
    return repository
        .findByRequestIdAndRoundIndex(requestId, index)
        .orElseThrow(
            () -> new NotFoundException("Round %d for request %d not found", index, requestId));
  }
}
