package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_ROUND_UPDATE;

import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestRoundTransaction {

  private final RequestRoundRepository requestRoundRepository;
  private final RequestRepository requestRepository;
  private final AuditService auditService;

  @Transactional
  public RequestRound updateRequestRound(RequestRound round) {
    var savedRound = requestRoundRepository.save(round);
    auditService.auditEvent(REQUEST_ROUND_UPDATE, toInfo(round));
    return savedRound;
  }

  private static Map<String, Object> toInfo(RequestRound round) {
    var request = round.getRequest();
    var status = round.getStatus();
    return Map.of(
        "requestId",
        request.getId(),
        "roundId",
        round.getId(),
        "index",
        round.getRoundIndex(),
        "startDate",
        round.getStartDate().toString(),
        "endDate",
        round.getEndDate().toString(),
        "statusId",
        status.getId());
  }
}
